package nodes.memoization

import java.util.concurrent.CountDownLatch
import nodes.helpers.IScope
import nodes.legalNeighbours.FactoryIdToFactory
import nodes.memoization.MemoizeScopeToNeighbours.mapOfNeighboursToJson
import play.api.libs.json._
import scala.language.implicitConversions

class MemoizeScopeToNeighbours(private var cache: Map[String, Either[CountDownLatch, Boolean]] = Map.empty[String, Either[CountDownLatch, Boolean]],
                               private val versioning: String
                                )
                              (implicit intToFactory: FactoryIdToFactory)
  extends Memoize2Impl[IScope, Int, Boolean](cache, versioning)(mapOfNeighboursToJson) {

  override def f(scope: IScope, neighbourId: Int): Boolean = {
    scope.hasHeightRemaining && {
      val possibleNeighbourIds = intToFactory.convert(neighbourId).neighbourIds
      possibleNeighbourIds.isEmpty ||
        possibleNeighbourIds.exists { possNeighbourId =>
          missing(key1 = scope.decrementHeight, key2 = possNeighbourId)
        }
    }
  }
}

object MemoizeScopeToNeighbours {

  private implicit val eitherLatchOrNeighboursToJson = new Writes[Either[CountDownLatch, Boolean]] {
    def writes(state: Either[CountDownLatch, Boolean]): JsValue =
      state.fold(
        countDownLatch => ???, // Should be filtered out at a higher level so that we do not store incomplete calculations.
        neighbours => JsBoolean(neighbours)
      )
  }

  private implicit val mapOfNeighboursToJson = new Writes[Map[String, Either[CountDownLatch, Boolean]]] {
    def writes(cache: Map[String, Either[CountDownLatch, Boolean]]): JsValue = {
      val filtered = cache.filter {
        case (k, v) => v.isRight // Only completed values.
      }.
        map {
        case (k, v) => k -> v // Json keys must be strings.
      }
      Json.toJson(filtered)
    }
  }

  implicit def readsMemoizeScopeToNeighbours(versioning: String)(implicit factoryIdToFactory: FactoryIdToFactory): Reads[MemoizeScopeToNeighbours] =
    (__ \ "versioning").read[String].flatMap[MemoizeScopeToNeighbours] {
      case versioningFromFile =>
        require(versioningFromFile == versioning, "version info from file did not match the intended versioning")
        (__ \ "cache").read[Map[String, Boolean]].map {
        keyValueMap =>
          val cache = keyValueMap.map {
            case (k, v) => k -> Right[CountDownLatch, Boolean](v)
          }

          new MemoizeScopeToNeighbours(cache, versioningFromFile)
      }
    }
}