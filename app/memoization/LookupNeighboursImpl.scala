package memoization

import com.google.inject.Inject
import models.common.IScope
import replaceEmpty._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

final class LookupNeighboursImpl @Inject()(factoryIdToFactory: FactoryLookup, neighboursRepository: NeighboursRepository) extends LookupNeighbours {

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ReplaceEmpty] = {
    val neighbourValues = neighbours.map { neighbourId =>
      neighboursRepository.apply(key1 = scope, key2 = neighbourId). // Get value from repository
        map(value => neighbourId -> value) // Convert to ReplaceEmpty
    }

    val result = Future.sequence(neighbourValues).map {
      _.filter {
        case (key: Int, value: Boolean) => value
      }.map {
        case (key: Int, value: Boolean) => factoryIdToFactory.convert(key)
      }
    }
    Await.result(result, Duration.Inf)
  }

  override def fetch(scope: IScope, currentNode: Int): Seq[Int] = {
    val factory = factoryIdToFactory.convert(currentNode)
    fetch(scope = scope, neighbours = factory.neighbourIds).
      map(factoryIdToFactory.convert)
  }
}