package memoization

import com.google.inject.Inject
import models.common.IScope
import replaceEmpty._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class LookupNeighboursImpl @Inject()(factoryIdToFactory: FactoryLookup, neighboursRepository: NeighboursRepository) extends LookupNeighbours {

  override def fetch(scope: IScope, neighbours: Seq[Int]): Future[Seq[ReplaceEmpty]] = {
    fetchFromRepository(scope, neighbours).map(_.map(factoryIdToFactory.convert))
  }

  override def fetch(scope: IScope, currentNode: Int): Future[Seq[Int]] = {
    val factory = factoryIdToFactory.convert(currentNode)
    fetchFromRepository(scope, factory.neighbourIds)
  }

  private def fetchFromRepository(scope: IScope, neighbours: Seq[Int]): Future[Seq[Int]] = {
    val neighbourValues = neighbours.map { neighbourId =>
      neighboursRepository.apply(key1 = scope, key2 = neighbourId). // Get value from repository
        map(value => neighbourId -> value) // Convert to ReplaceEmpty
    }

    Future.sequence(neighbourValues).map {
      _.filter {
        case (key: Int, value: Boolean) => value
      }.map {
        case (key: Int, value: Boolean) => key
      }
    }
  }
}