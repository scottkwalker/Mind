package memoization

import com.google.inject.Inject
import decision._
import models.common.IScope
import models.domain.scala.FactoryLookup
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class LookupChildrenWithFuturesImpl @Inject()(
                                                     override val factoryLookup: FactoryLookup,
                                                     repository: Memoize2[IScope, PozInt, Future[Boolean]]
                                                     ) extends LookupChildrenWithFutures {

  override def get(scope: IScope, childrenToChooseFrom: Set[PozInt]): Future[Set[Decision]] =
    fetchFromRepository(scope, childrenToChooseFrom).map(_.map(factoryLookup.convert))

  private def fetchFromRepository(scope: IScope, neighbours: Set[PozInt]): Future[Set[PozInt]] = {
    val neighbourValues = Future.traverse(neighbours) { neighbourId =>
      repository.apply(key1 = scope, key2 = neighbourId). // Boolean value from repository.
        map(value => neighbourId -> value) // Convert to key-value pairs
    }

    neighbourValues.map {
      _.collect {
        case (key: PozInt, value: Boolean) if value => key
      }
    }
  }

  override def get(scope: IScope, parent: PozInt): Future[Set[PozInt]] = {
    val nodesToChooseFrom = {
      val factory = factoryLookup.convert(parent)
      factory.nodesToChooseFrom
    }
    fetchFromRepository(scope, nodesToChooseFrom)
  }

  override def size: Int = repository.size
}