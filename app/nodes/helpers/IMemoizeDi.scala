package nodes.helpers

/**
 * Created by valtechuk on 31/12/2013.
 */
trait IMemoizeDi[TInput, TResult] {
  val store: mutable.Map[TInput, TResult]
}
