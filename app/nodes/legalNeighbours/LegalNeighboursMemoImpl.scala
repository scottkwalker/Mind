package nodes.legalNeighbours

import com.google.inject.Inject
import models.common.IScope
import nodes._
import nodes.helpers.ICreateChildNodes
import nodes.memoization.MemoizeScopeToNeighbours

final class LegalNeighboursMemoImpl @Inject()(factoryIdToFactory: FactoryIdToFactory) extends LegalNeighboursMemo {

  private val memo = {
    val versioning = s"${AddOperatorFactoryImpl.id}|${FunctionMFactoryImpl.id}|${IntegerMFactoryImpl.id}|${NodeTreeFactoryImpl.id}|${ObjectDefFactoryImpl.id}|${ValDclInFunctionParamFactoryImpl.id}|${ValueRefFactoryImpl.id}"
    new MemoizeScopeToNeighbours(versioning = versioning, factoryIdToFactory = factoryIdToFactory)
  }

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes] = {
    neighbours.
      filter(neighbour => memo.apply(key1 = scope, key2 = neighbour)). // Remove neighbours that cannot terminate at this scope.
      map(factoryIdToFactory.convert)
  }

  override def fetch(scope: IScope, currentNode: Int): Seq[Int] = {
    val factory = factoryIdToFactory.convert(currentNode)
    fetch(scope = scope, neighbours = factory.neighbourIds).
      map(factoryIdToFactory.convert)
  }
}