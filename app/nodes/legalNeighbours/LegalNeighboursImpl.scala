package nodes.legalNeighbours

import java.util.concurrent.CountDownLatch

import com.google.inject.Inject
import nodes.helpers.{ICreateChildNodes, IScope}
import nodes.memoization.Memoize2Impl
import play.api.libs.json.Json._
import play.api.libs.json.{JsValue, Json, Writes}

final class LegalNeighboursImpl @Inject()(intToFactory: FactoryIdToFactory) extends LegalNeighbours {
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

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes] = {
    val memo = new Memoize2Impl[IScope, Seq[Int], Seq[Int]] {
      def f(scope: IScope, neighbours: Seq[Int]): Seq[Int] = {
        if (scope.hasDepthRemaining) neighbours.filter {
          neighbourId =>
            val factory = intToFactory.convert(neighbourId)
            factory.neighbourIds.isEmpty || missing(key = scope.incrementDepth, neighbours = factory.neighbourIds).length > 0
        }
        else Seq.empty
      }
    }
    val validForThisScope = memo.apply(key = scope, neighbours = neighbours)
    validForThisScope.map(intToFactory.convert)
  }
}