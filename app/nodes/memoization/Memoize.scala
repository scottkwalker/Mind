package nodes.memoization

import play.api.libs.json.{Writes, Format}
import java.util.concurrent.CountDownLatch


// The code below is a mashup between an example from stackoverflow and code originally based on com.twitter.util.Memoize.
// I changed it from an object to a class.


object Memoize {
  /**
   * Memoize a unary (single-argument) function.
   *
   * @param f the unary function to memoize
   */
  def memoize[TInput, TOutput](f: TInput => TOutput)
                              (implicit cacheFormat: Writes[Map[TInput, Either[CountDownLatch, TOutput]]]): Memoize1[TInput, TOutput] =
    new Memoize1Impl(f)

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
  def Y[TInput, TOutput](f: Memoize1[TInput, TOutput] => TInput => TOutput)
                        (implicit cacheFormat: Writes[Map[TInput, Either[CountDownLatch, TOutput]]]): Memoize1[TInput, TOutput] = {
    lazy val yf: Memoize1[TInput, TOutput] = memoize(f(yf)(_))
    yf
  }
}

// TODO Memoize needs play json serialization
// TODO BitSets could be used as the memoize Type for memo http://docs.scala-lang.org/overviews/collections/concrete-immutable-collection-classes.html so that it requires less memory.