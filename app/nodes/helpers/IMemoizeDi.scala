package nodes.helpers

import scala.collection.mutable

trait IMemoizeDi[TInput, TResult] {
  val store: mutable.Map[TInput, TResult]
}
