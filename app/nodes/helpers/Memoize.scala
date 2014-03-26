package nodes.helpers

import scala.collection.mutable
import com.google.inject.Inject


case class MemoizeDi[TKey, TValue] @Inject()() extends IMemoizeDi[TKey, TValue] {
  private val store: mutable.Map[TKey, TValue] = mutable.Map.empty[TKey, TValue]

  def getOrElseUpdate(key: TKey, op: TValue): TValue = store.getOrElseUpdate(key, op)

  def size = store.size
}

/**
 * A memoized unary function.
 *
 * @param f A unary function to memoize
 *          T the argument type
 *          R the return type
 */
class Memoize1[-TInput, +TOutput](f: TInput => TOutput) extends (TInput => TOutput) {
  // map that stores (argument, result) pairs
  private[this] val vals = mutable.Map.empty[TInput, TOutput]

  // Given an argument x,
  //   If vals contains x return vals(x).
  //   Otherwise, update vals so that vals(x) == f(x) and return f(x).
  def apply(x: TInput): TOutput = vals getOrElseUpdate(x, f(x))
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