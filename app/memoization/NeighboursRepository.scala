package memoization

import java.util.concurrent.CountDownLatch
import com.google.inject.Inject
import memoization.NeighboursRepository.writesNeighboursRepository
import models.common.IScope
import play.api.libs.json._
import utils.Timeout.finiteTimeout
import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions

class NeighboursRepository @Inject()(factoryLookup: FactoryLookup)
  extends Memoize2Impl[IScope, Int, Boolean](factoryLookup.version)(writesNeighboursRepository) {

  def funcCalculate(scope: IScope, neighbourId: Int): Future[Boolean] =
    async {
      if (scope.hasHeightRemaining) {
        val possibleNeighbourIds = factoryLookup.convert(neighbourId).nodesToChooseFrom
        if (possibleNeighbourIds.isEmpty) true
        else await {
          // TODO can this use Observable to turn it into a stream of futures and then it wouldn't need to Await for all the results to complete.
          val futures = possibleNeighbourIds.map { possNeighbourId =>
            missing(key1 = scope.decrementHeight, key2 = possNeighbourId)
          }
          Future.sequence(futures).map(_.contains(true))
        }
      }
      else false
    }
}

object NeighboursRepository {

  private[memoization] implicit val writesNeighboursRepository = new Writes[Map[String, Either[CountDownLatch, Future[Boolean]]]] {
    def writes(cache: Map[String, Either[CountDownLatch, Future[Boolean]]]): JsValue = {
      def computedKeyValues: Map[String, Boolean] = cache.flatMap {
        case (key, Right(value)) if value.isCompleted =>
          val computed = Await.result(value, finiteTimeout) // It should be OK to use blocking Await here are the result is already computed so should instantly be returned.
          Some(key -> computed) // Only store the computed values (the 'right-side').
        case _ => None
      }
      Json.toJson(computedKeyValues)
    }
  }

  private[memoization] implicit def readsNeighboursRepository(factoryLookup: FactoryLookup): Reads[NeighboursRepository] =
    (__ \ "versioning").read[String].flatMap[NeighboursRepository] {
      case versioningFromFile =>
        require(versioningFromFile == factoryLookup.version, s"version info from file ($versioningFromFile) did not match the intended versioning (${factoryLookup.version})")
        (__ \ "cache").read[Map[String, Boolean]].map {
          keyValueMap =>
            val cache = keyValueMap.map {
              case (key, value) => key -> Right[CountDownLatch, Future[Boolean]](Future.successful(value))
            }

            val neighboursRepository = new NeighboursRepository(factoryLookup)
            neighboursRepository.cache = cache // Overwrite the empty cache with values from the file.
            neighboursRepository
        }
    }
}