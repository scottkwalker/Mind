package nodes.helpers

import java.util.concurrent.CountDownLatch
import scala.annotation.tailrec

// The code below is a mashup between an example from stackoverflow and code originally based on com.twitter.util.Memoize.
// I changed it from an object to a class.
/**
 * A memoized unary function.
 *
 * @param f A unary function to memoize
 *          T the argument type
 *          R the return type
 */
class Memoize1[-TInput, +TOutput](f: TInput => TOutput) extends (TInput => TOutput) {
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
  private[this] var vals = Map.empty[TInput, Either[CountDownLatch, TOutput]]

  /**
   * What to do if we do not find the value already in the memo
   * table.
   */
  @tailrec private[this] def missing(a: TInput): TOutput =
    synchronized {
      // With the lock, check to see what state the value is in.
      vals.get(a) match {
        case None =>
          // If it's missing, then claim the slot by putting in a CountDownLatch that will be completed when the value is
          // available.
          val latch = new CountDownLatch(1)
          vals = vals + (a -> Left(latch))

          // The latch wrapped in Left indicates that the value needs to be computed in this thread, and then the
          // latch counted down.
          Left(latch)

        case Some(other) =>
          // This is either the latch that will indicate that the work has been done, or the computed value.
          Right(other)
      }
    } match {
      case Right(Right(b)) => b // The computation is already done.
      case Right(Left(latch)) =>
        // Someone else is doing the computation.
        latch.await()

        // This recursive call will happen when there is an exception computing the value, or if the value is
        // currently being computed.
        missing(a)

      case Left(latch) =>
        // Compute the value outside of the synchronized block.
        val b =
          try {
            f(a)
          } catch {
            case t: Throwable =>
              // If there was an exception running the computation, then we need to make sure we do not
              // starve any waiters before propagating the exception.
              synchronized {
                vals = vals - a
              }
              latch.countDown()
              throw t
          }

        // Update the memo table to indicate that the work has been done, and signal to any waiting threads that the
        // work is complete.
        synchronized {
          vals = vals + (a -> Right(b))
        }
        latch.countDown()
        b
    }

  def getOrElseUpdate(a: TInput): TOutput =
  // Look in the (possibly stale) memo table. If the value is present, then it is guaranteed to be the final value. If it
  // is absent, call missing() to determine what to do.
    vals.get(a) match {
      case Some(Right(b)) => b
      case _ => missing(a)
    }

  def getOpt(a: TInput): Option[TOutput] =
  // If the value is present, then return the calculated value.
  // Else it is not yet calculated.
    vals.get(a) match {
      case Some(Right(b)) => Some(b)
      case _ => None
    }

  def apply(x: TInput): TOutput = getOrElseUpdate(x)

  def apply(x: Some[TInput]): Option[TOutput] = getOpt(x.get)
}

object Memoize {
  /**
   * Memoize a unary (single-argument) function.
   *
   * @param f the unary function to memoize
   */
  def memoize[TInput, TOutput](f: TInput => TOutput): (TInput => TOutput) = new Memoize1(f)

  /*
    /**
     * Memoize a binary (two-argument) function.
     *
     * @param f the binary function to memoize
     *
     *          This works by turning a function that takes two arguments of type
     *          T1 and T2 into a function that takes a single argument of type
     *          (T1, T2), memoizing that "tupled" function, then "untupling" the
     *          memoized function.
     */
    def memoize[T1, T2, R](f: (T1, T2) => R): ((T1, T2) => R) =
      Function.untupled(memoize(f.tupled))

    /**
     * Memoize a ternary (three-argument) function.
     *
     * @param f the ternary function to memoize
     */
    def memoize[T1, T2, T3, R](f: (T1, T2, T3) => R): ((T1, T2, T3) => R) =
      Function.untupled(memoize(f.tupled))

    // ... more memoize methods for higher-arity functions ...
  */
  /**
   * Fixed-point combinator (for memoizing recursive functions).
   */
  def Y[TInput, TOutput](f: (TInput => TOutput) => TInput => TOutput): (TInput => TOutput) = {
    lazy val yf: (TInput => TOutput) = memoize(f(yf)(_))
    yf
  }
}