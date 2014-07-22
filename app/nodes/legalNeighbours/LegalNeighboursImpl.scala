package nodes.legalNeighbours

import com.google.inject.Inject
import nodes.helpers.{ICreateChildNodes, IScope}
import nodes.memoization.MemoizeScopeToNeighbours

final class LegalNeighboursImpl @Inject()(implicit intToFactory: FactoryIdToFactory) extends LegalNeighbours {
  val memo = new MemoizeScopeToNeighbours() // TODO we could IoC this

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes] = {
    neighbours.
      filter(neighbour => memo.apply(key1 = scope, key2 = neighbour)). // Remove neighbours that cannot terminate at this scope.
      map(intToFactory.convert)
  }
}