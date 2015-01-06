package memoization

import com.google.inject.Inject
import models.common.IScope
import models.domain.scala.FactoryLookup
import replaceEmpty._
import utils.PozInt
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class LookupChildrenImpl @Inject()(override val factoryLookup: FactoryLookup, neighboursRepository: NeighboursRepository) extends LookupChildren {

  override def fetch(scope: IScope, childrenToChooseFrom: Set[PozInt]): Future[Set[ReplaceEmpty]] = {
    fetchFromRepository(scope, childrenToChooseFrom).map(_.map(factoryLookup.convert))
  }

  override def fetch(scope: IScope, parent: PozInt): Future[Set[PozInt]] = {
    val factory = factoryLookup.convert(parent)
    val nodesToChooseFrom = factory.nodesToChooseFrom
    fetchFromRepository(scope, nodesToChooseFrom)
  }

  private def fetchFromRepository(scope: IScope, neighbours: Set[PozInt]): Future[Set[PozInt]] = {
    val neighbourValues = neighbours.map { neighbourId =>
      neighboursRepository.apply(key1 = scope, key2 = neighbourId). // Get value from repository
        map(value => neighbourId -> value) // Convert to ReplaceEmpty
    }

    Future.sequence(neighbourValues).map {
      _.filter {
        case (_: PozInt, value: Boolean) => value
      }.map {
        case (key: PozInt, _: Boolean) => key
      }
    }
  }
}