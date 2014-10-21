package memoization

import com.google.inject.Inject
import models.common.IScope
import replaceEmpty._

final class LookupNeighboursImpl @Inject()(factoryIdToFactory: FactoryLookup, neighboursRepository: NeighboursRepository) extends LookupNeighbours {

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ReplaceEmpty] = {
    neighbours.
      filter(neighbour => neighboursRepository.apply(key1 = scope, key2 = neighbour)). // Remove neighbours that cannot terminate at this scope.
      map(factoryIdToFactory.convert)
  }

  override def fetch(scope: IScope, currentNode: Int): Seq[Int] = {
    val factory = factoryIdToFactory.convert(currentNode)
    fetch(scope = scope, neighbours = factory.neighbourIds).
      map(factoryIdToFactory.convert)
  }
}