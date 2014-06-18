package nodes.memoization

import java.util.concurrent.CountDownLatch
import scala.annotation.tailrec
import play.api.libs.json.{Writes, Format, JsValue}
import nodes.helpers.{IScope, JsonSerialiser}

/**
 * A memoized unary function.
 *
 * @param f A unary function to memoize
 *          T the argument type
 *          R the return type
 */
final class Memoize1Impl[-TInput, +TOutput](f: TInput => TOutput)
                                           (implicit cacheFormat: Writes[Map[TInput, Either[CountDownLatch, TOutput]]]) extends Memoize1[TInput, TOutput] {
  /**
   * Thread-safe memoization for a function.
   *
   * This works like a lazy val indexed by the input value. The memo
   * is held as part of the state of the returned function, so keeping
   * a reference to the function will keep a reference to the
   * (unbounded) memo table. The memo table will never forget a
   * result, and will retain a reference to the corresponding input
   * values as well.
   *
   * If the computation has side-effects, they will happen exactly
   * once per input, even if multiple threads attempt to memoize the
   * same input at one time, unless the computation throws an
   * exception. If an exception is thrown, then the result will not be
   * stored, and the computation will be attempted again upon the next
   * access. Only one value will be computed at a time. The overhead
   * required to ensure that the effects happen only once is paid only
   * in the case of a miss (once per input over the life of the memo
   * table). Computations for different input values will not block
   * each other.
   *
   * The combination of these factors means that this method is useful
   * for functions that will only ever be called on small numbers of
   * inputs, are expensive compared to a hash lookup and the memory
   * overhead, and will be called repeatedly.
   */
  private[this] var cache = Map.empty[TInput, Either[CountDownLatch, TOutput]]

  /**
   * What to do if we do not find the value already in the memo
   * table.
   */
  @tailrec private[this] def missing(key: TInput): TOutput =
    synchronized {
      // With the lock, check to see what state the value is in.
      cache.get(key) match {
        case None =>
          // If it's missing, then claim the slot by putting in a CountDownLatch that will be completed when the value is
          // available.
          val latch = new CountDownLatch(1)
          cache = cache + (key -> Left(latch))

          // The latch wrapped in Left indicates that the value needs to be computed in this thread, and then the
          // latch counted down.
          Left(latch)
        case Some(other) =>
          // This is either the latch that will indicate that the work has been done, or the computed value.
          Right(other)
      }
    } match {
      case Right(Right(computed)) => computed // The computation is already done.
      case Right(Left(latch)) =>
        // Someone else is doing the computation.
        latch.await()

        // This recursive call will happen when there is an exception computing the value, or if the value is
        // currently being computed.
        missing(key)
      case Left(latch) =>
        // Compute the value outside of the synchronized block.
        val calculated =
          try {
            f(key)
          } catch {
            case t: Throwable =>
              // If there was an exception running the computation, then we need to make sure we do not
              // starve any waiters before propagating the exception.
              synchronized {
                cache = cache - key
              }
              latch.countDown()
              throw t
          }

        // Update the memo table to indicate that the work has been done, and signal to any waiting threads that the
        // work is complete.
        synchronized {
          cache = cache + (key -> Right(calculated))
        }
        latch.countDown()
        calculated
    }

  override def apply(key: TInput): TOutput = // Look in the (possibly stale) memo table. If the value is present, then it is guaranteed to be the final value.
  // If it is absent, call missing() to determine what to do.
    cache.get(key) match {
      case Some(Right(b)) => b
      case _ => missing(key)
    }

  override def write: JsValue = {
    val jsonSerialiser = new JsonSerialiser
    jsonSerialiser.serialize(cache)
  }
}




/**
 * A memoized unary function.
 *
 * @param f A unary function to memoize
 *          T the argument type
 *          R the return type
 */
abstract class Memoize2Impl[IScope, Seq[Int]]()
                                           (implicit cacheFormat: Writes[Map[IScope, Either[CountDownLatch, Seq[Int]]]]) {
  /**
   * Thread-safe memoization for a function.
   *
   * This works like a lazy val indexed by the input value. The memo
   * is held as part of the state of the returned function, so keeping
   * a reference to the function will keep a reference to the
   * (unbounded) memo table. The memo table will never forget a
   * result, and will retain a reference to the corresponding input
   * values as well.
   *
   * If the computation has side-effects, they will happen exactly
   * once per input, even if multiple threads attempt to memoize the
   * same input at one time, unless the computation throws an
   * exception. If an exception is thrown, then the result will not be
   * stored, and the computation will be attempted again upon the next
   * access. Only one value will be computed at a time. The overhead
   * required to ensure that the effects happen only once is paid only
   * in the case of a miss (once per input over the life of the memo
   * table). Computations for different input values will not block
   * each other.
   *
   * The combination of these factors means that this method is useful
   * for functions that will only ever be called on small numbers of
   * inputs, are expensive compared to a hash lookup and the memory
   * overhead, and will be called repeatedly.
   */

  def f: (IScope, Seq[Int]) => Seq[Int]

  private[this] var cache = Map.empty[IScope, Either[CountDownLatch, Seq[Int]]]

  /**
   * What to do if we do not find the value already in the memo
   * table.
   */
  @tailrec private[this] def missing(key: IScope, neighbours: Seq[Int]): Seq[Int] = {
    synchronized {
      // With the lock, check to see what state the value is in.
      cache.get(key) match {
        case None =>
          // If it's missing, then claim the slot by putting in a CountDownLatch that will be completed when the value is
          // available.
          val latch = new CountDownLatch(1)
          cache += (key -> Left(latch))

          // The latch wrapped in Left indicates that the value needs to be computed in this thread, and then the
          // latch counted down.
          Left(latch)
        case Some(other) =>
          // This is either the latch that will indicate that the work has been done, or the computed value.
          Right(other)
      }
    } match {
      case Right(Right(computed)) => computed // The computation is already done.
      case Right(Left(latch)) =>
        // Someone else is doing the computation.
        latch.await()
        // This recursive call will happen when there is an exception computing the value, or if the value is
        // currently being computed.
        missing(key, neighbours)
      case Left(latch) =>
        // Compute the value outside of the synchronized block.
        val calculated =
          try {
            f(key, neighbours)
          } catch {
            case t: Throwable =>
              // If there was an exception running the computation, then we need to make sure we do not
              // starve any waiters before propagating the exception.
              synchronized {
                cache -= key
              }
              latch.countDown()
              throw t
          }

        // Update the memo table to indicate that the work has been done, and signal to any waiting threads that the
        // work is complete.
        synchronized {
          cache += (key -> Right(calculated))
        }
        latch.countDown()
        calculated
    }
  }

  def apply(key: IScope, neighbours: Seq[Int]): Seq[Int] = // Look in the (possibly stale) memo table. If the value is present, then it is guaranteed to be the final value.
    // If it is absent, call missing() to determine what to do.
    cache.get(key) match {
      case Some(Right(b)) => b
      case _ => f(key, neighbours)
    }
}