package nodes.legalNeighbours

import nodes.helpers.{ICreateChildNodes, IScope}
import com.google.inject.Inject
import nodes.memoization.{Memoize, Memoize1}
import play.api.libs.json.{Json, JsValue, Writes}
import java.util.concurrent.CountDownLatch
import play.api.libs.json.Json._

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

  private def legalForScope(scope: IScope, neighbours: Seq[Int]): Seq[Int] = {
    val memo: Memoize1[IScope, Seq[Int]] = {
      def inner(f: Memoize1[IScope, Seq[Int]])(innerScope: IScope): Seq[Int] = {
        if (innerScope.hasDepthRemaining) neighbours.filter {
          neighbourId =>
            val factory = intToFactory.convert(neighbourId)
            factory.neighbourIds.isEmpty || legalForScope(scope = innerScope.incrementDepth, neighbours = factory.neighbourIds).length > 0
        }
        else Seq.empty
      }

      Memoize.Y(inner)
    }

    memo(scope.incrementDepth).intersect(neighbours) // Only return legal moves that are neighbours
  }

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes] = legalForScope(scope, neighbours).map(intToFactory.convert)
}


