package nodes.legalNeighbours

import java.util.concurrent.CountDownLatch

import nodes.helpers.IScope
import nodes.legalNeighbours.MemoizeScopeToNeighbours.mapWrites
import nodes.memoization.Memoize2Impl
import play.api.libs.json.Json._
import play.api.libs.json.{JsValue, Json, Writes}

class MemoizeScopeToNeighbours()(implicit intToFactory: FactoryIdToFactory) extends Memoize2Impl[IScope, Seq[Int], Seq[Int]]()(mapWrites) {
  override def f(scope: IScope, neighbours: Seq[Int]): Seq[Int] = {
    if (scope.hasDepthRemaining) neighbours.filter {
      neighbourId =>
        val factory = intToFactory.convert(neighbourId)
        factory.neighbourIds.isEmpty || missing(key = scope.incrementDepth, t2 = factory.neighbourIds).length > 0
    }
    else Seq.empty
  }
}

object MemoizeScopeToNeighbours {
  implicit val mapWrites = new Writes[Map[IScope, Either[CountDownLatch, Seq[Int]]]] {
    implicit val eitherWrites = new Writes[Either[CountDownLatch, Seq[Int]]] {
      def writes(o: Either[CountDownLatch, Seq[Int]]): JsValue = obj(
        o.fold(
          countDownLatchContent => ???,
          intContent => "intContent" -> Json.toJson(intContent)
        )
      )
    }

    def writes(o: Map[IScope, Either[CountDownLatch, Seq[Int]]]): JsValue = {
      val keyAsString = o.filter(kv => kv._2.isRight). // Only completed values.
        map(kv => kv._1.toString -> kv._2) // Json keys must be strings.
      Json.toJson(keyAsString)
    }
  }
}