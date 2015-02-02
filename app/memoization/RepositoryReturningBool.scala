package memoization

import java.util.concurrent.CountDownLatch

import com.google.inject.Inject
import memoization.RepositoryReturningBool.writes
import models.common.IScope
import models.domain.scala.FactoryLookup
import play.api.libs.json._
import utils.PozInt

import scala.language.implicitConversions

final class RepositoryReturningBool @Inject()(factoryLookup: FactoryLookup)
  extends Memoize2Impl[IScope, PozInt, Boolean](factoryLookup.version)(writes) {

  override def funcCalculate(scope: IScope, neighbourId: PozInt): Boolean =
    if (scope.hasHeightRemaining) {
      val possibleNeighbourIds = factoryLookup.convert(neighbourId).nodesToChooseFrom
      if (possibleNeighbourIds.isEmpty) true
      else {
        val futures = possibleNeighbourIds.map { possNeighbourId =>
          missing(key1 = scope.decrementHeight, key2 = possNeighbourId)
        }
        futures.contains(true)
      }
    }
    else false

  override def size: Int = cache.size
}

object RepositoryReturningBool {

  private[memoization] implicit val writes = new Writes[Map[String, Either[CountDownLatch, Boolean]]] {
    def writes(cache: Map[String, Either[CountDownLatch, Boolean]]): JsValue = {
      def computedKeyValues: Map[String, Boolean] = cache.flatMap {
        case (key, Right(value)) =>
          Some(key -> value)
        case _ => None
      }
      Json.toJson(computedKeyValues)
    }
  }

  private[memoization] implicit def reads(factoryLookup: FactoryLookup): Reads[RepositoryReturningBool] =
    (__ \ "versioning").read[String].flatMap[RepositoryReturningBool] {
      case versioningFromFile =>
        require(versioningFromFile == factoryLookup.version, s"version info from file ($versioningFromFile) did not match the intended versioning (${factoryLookup.version})")
        (__ \ "cache").read[Map[String, Boolean]].map {
          keyValueMap =>
            val cache = keyValueMap.map {
              case (key, value) => key -> Right[CountDownLatch, Boolean](value)
            }

            val neighboursRepository = new RepositoryReturningBool(factoryLookup)
            neighboursRepository.cache = cache // Overwrite the empty cache with values from the file.
            neighboursRepository
        }
    }
}