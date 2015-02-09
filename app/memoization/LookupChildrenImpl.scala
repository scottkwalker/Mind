package memoization

import com.google.inject.Inject
import models.common.IScope
import models.domain.scala.FactoryLookup
import decision.Decision
import utils.PozInt

final class LookupChildrenImpl @Inject()(
                                          override val factoryLookup: FactoryLookup,
                                          repository: Memoize2WithSet[IScope, PozInt]
                                          ) extends LookupChildren {

  override def get(scope: IScope, childrenToChooseFrom: Set[PozInt]): Set[Decision] =
    fetchFromRepository(scope, childrenToChooseFrom).
      map(factoryLookup.convert)

  override def get(scope: IScope, parent: PozInt): Set[PozInt] = {
    val factory = factoryLookup.convert(parent)
    val nodesToChooseFrom = factory.nodesToChooseFrom
    fetchFromRepository(scope, nodesToChooseFrom)
  }

  override def size: Int = repository.size

  private def fetchFromRepository(scope: IScope, neighbours: Set[PozInt]): Set[PozInt] =
    neighbours.
      filter(neighbourId => repository.apply(key1 = scope, key2 = neighbourId)) // Get value from repository
}