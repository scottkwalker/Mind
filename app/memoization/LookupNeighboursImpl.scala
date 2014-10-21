package memoization

import com.google.inject.Inject
import replaceEmpty._
import models.common.IScope

final class LookupNeighboursImpl @Inject()(factoryIdToFactory: FactoryLookup) extends LookupNeighbours {

  private val memo = {
    val versioning = s"${AddOperatorFactoryImpl.id}|${FunctionMFactoryImpl.id}|${IntegerMFactoryImpl.id}|${NodeTreeFactoryImpl.id}|${ObjectDefFactoryImpl.id}|${ValDclInFunctionParamFactoryImpl.id}|${ValueRefFactoryImpl.id}"
    new NeighboursRepository(versioning = versioning, factoryIdToFactory = factoryIdToFactory)
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