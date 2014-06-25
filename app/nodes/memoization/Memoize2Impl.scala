package nodes.memoization

import java.util.concurrent.CountDownLatch

import nodes.helpers.JsonSerialiser
import play.api.libs.json.{JsValue, Writes}

import scala.annotation.tailrec

abstract class Memoize2Impl[-T1, -T2, +T3]()
                                          (implicit cacheFormat: Writes[Map[T1, Either[CountDownLatch, T3]]]) extends Memoize2[T1, T2, T3] {
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

  private[this] var cache = Map.empty[T1, Either[CountDownLatch, T3]]

  /**
   * What to do if we do not find the value already in the memo
   * table.
   */
  @tailrec protected final def missing(key: T1, t2: T2): T3 = {
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
        missing(key, t2)
      case Left(latch) =>
        // Compute the value outside of the synchronized block.
        val calculated =
          try {
            f(key, t2)
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

  def apply(key: T1, t2: T2): T3 = // Look in the (possibly stale) memo table. If the value is present, then it is guaranteed to be the final value.
  // If it is absent, call missing() to determine what to do.
    cache.get(key) match {
      case Some(Right(b)) => b
      case _ => f(key, t2)
    }

  override def write: JsValue = {
    val jsonSerialiser = new JsonSerialiser
    jsonSerialiser.serialize(cache)
  }
}
