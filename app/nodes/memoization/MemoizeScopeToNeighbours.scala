package nodes.memoization

import java.util.concurrent.CountDownLatch

import nodes.helpers.IScope
import nodes.legalNeighbours.FactoryIdToFactory
import play.api.libs.json.Json._
import play.api.libs.json.{JsValue, Json, Writes}

class MemoizeScopeToNeighbours()(implicit intToFactory: FactoryIdToFactory) extends Memoize2Impl[IScope, Seq[Int], Seq[Int]]()(mapWrites) {
  override def f(scope: IScope, neighbours: Seq[Int]): Seq[Int] = {
    if (scope.hasDepthRemaining) neighbours.filter {
      neighbourId =>
        val factory = intToFactory.convert(neighbourId)
        factory.neighbourIds.isEmpty || missing(key1 = scope.incrementDepth, key2 = factory.neighbourIds).length > 0
    }
    else Seq.empty
  }
}

object MemoizeScopeToNeighbours {
  implicit val mapWrites = new Writes[Map[String, Either[CountDownLatch, Seq[Int]]]] {
    implicit val eitherWrites = new Writes[Either[CountDownLatch, Seq[Int]]] {
      def writes(state: Either[CountDownLatch, Seq[Int]]): JsValue = obj(
        state.fold(
          countDownLatchContent => ???,
          intContent => "right" -> Json.toJson(intContent)
        )
      )
    }

    def writes(cache: Map[String, Either[CountDownLatch, Seq[Int]]]): JsValue = {
      val keyAsString = cache.filter(kv => kv._2.isRight). // Only completed values.
        map(kv => kv._1 -> kv._2) // Json keys must be strings.
      Json.toJson(keyAsString)
    }
  }
}