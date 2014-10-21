package memoization

import java.util.concurrent.CountDownLatch
import memoization.NeighboursRepository.writesNeighboursRepository
import models.common.IScope
import play.api.libs.json._
import scala.language.implicitConversions

class NeighboursRepository(private var cache: Map[String, Either[CountDownLatch, Boolean]] = Map.empty[String, Either[CountDownLatch, Boolean]],
                               private val versioning: String,
                               factoryIdToFactory: FactoryLookup)
  extends Memoize2Impl[IScope, Int, Boolean](cache, versioning)(writesNeighboursRepository) {

  override def f(scope: IScope, neighbourId: Int): Boolean = {
    scope.hasHeightRemaining && {
      val possibleNeighbourIds = factoryIdToFactory.convert(neighbourId).neighbourIds
      possibleNeighbourIds.isEmpty ||
        possibleNeighbourIds.exists { possNeighbourId =>
          missing(key1 = scope.decrementHeight, key2 = possNeighbourId)
        }
    }
  }
}

object NeighboursRepository {

  private implicit val writesNeighboursRepository = new Writes[Map[String, Either[CountDownLatch, Boolean]]] {
    def writes(cache: Map[String, Either[CountDownLatch, Boolean]]): JsValue = {
      val computedKeyValues = cache.flatMap {
        case (k, Right(v)) => Some(k -> v) // Only store the computed values (the 'right-side').
        case _ => None
      }
      Json.toJson(computedKeyValues)
    }
  }

  implicit def readsNeighboursRepository(versioning: String, factoryIdToFactory: FactoryLookup): Reads[NeighboursRepository] =
    (__ \ "versioning").read[String].flatMap[NeighboursRepository] {
      case versioningFromFile =>
        require(versioningFromFile == versioning, "version info from file did not match the intended versioning")
        (__ \ "cache").read[Map[String, Boolean]].map {
          keyValueMap =>
            val cache = keyValueMap.map {
              case (k, v) => k -> Right[CountDownLatch, Boolean](v)
            }

            new NeighboursRepository(cache, versioningFromFile, factoryIdToFactory)
        }
    }
}