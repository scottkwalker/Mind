package nodes.memoization

import java.util.concurrent.CountDownLatch

import nodes.helpers.IScope
import nodes.legalNeighbours.FactoryIdToFactory
import nodes.memoization.MemoizeScopeToNeighbours.mapOfNeighboursToJson
import play.api.libs.json.Json._
import play.api.libs.json.{JsValue, Json, Writes}

class MemoizeScopeToNeighbours()(implicit intToFactory: FactoryIdToFactory) extends Memoize2Impl[IScope, Seq[Int], Seq[Int]]()(mapOfNeighboursToJson) {
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
  private implicit val eitherLatchOrNeighboursToJson = new Writes[Either[CountDownLatch, Seq[Int]]] {
    private final val stateKey = "neighbours"
    def writes(state: Either[CountDownLatch, Seq[Int]]): JsValue = obj(
      state.fold(
        countDownLatch => ???, // Should be filtered out at a higher level so that we do not store incomplete calculations.
        neighbours => stateKey -> Json.toJson(neighbours)
      )
    )
  }
  
  private implicit val mapOfNeighboursToJson = new Writes[Map[String, Either[CountDownLatch, Seq[Int]]]] {
    def writes(cache: Map[String, Either[CountDownLatch, Seq[Int]]]): JsValue = {
      val filtered = cache.filter {
          case (k, v) => v.isRight // Only completed values.
        }.
        map{
          case (k, v) => k -> v // Json keys must be strings.
        }
      Json.obj("cache" -> Json.toJson(filtered))
    }
  }
}