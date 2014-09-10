package memoization

import com.google.inject.Inject
import factory._
import models.common.IScope

final class LegalNeighboursMemoImpl @Inject()(factoryIdToFactory: FactoryLookup) extends LegalNeighboursMemo {

  private val memo = {
    val versioning = s"${AddOperatorFactoryImpl.id}|${FunctionMFactoryImpl.id}|${IntegerMFactoryImpl.id}|${NodeTreeFactoryImpl.id}|${ObjectDefFactoryImpl.id}|${ValDclInFunctionParamFactoryImpl.id}|${ValueRefFactoryImpl.id}"
    new MemoizeScopeToNeighbours(versioning = versioning, factoryIdToFactory = factoryIdToFactory)
  }

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ReplaceEmpty] = {
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