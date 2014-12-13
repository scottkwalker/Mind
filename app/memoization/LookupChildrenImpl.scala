package memoization

import com.google.inject.Inject
import models.common.IScope
import replaceEmpty._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class LookupChildrenImpl @Inject()(factoryIdToFactory: FactoryLookup, neighboursRepository: NeighboursRepository) extends LookupChildren {

  override def fetch(scope: IScope, childrenToChooseFrom: Seq[Int]): Future[Seq[ReplaceEmpty]] = {
    fetchFromRepository(scope, childrenToChooseFrom).map(_.map(factoryIdToFactory.convert))
  }

  override def fetch(scope: IScope, parent: Int): Future[Seq[Int]] = {
    val factory = factoryIdToFactory.convert(parent)
    val nodesToChooseFrom = factory.nodesToChooseFrom
    fetchFromRepository(scope, nodesToChooseFrom)
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