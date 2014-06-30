package nodes.legalNeighbours

import com.google.inject.Inject
import nodes.helpers.{ICreateChildNodes, IScope}

final class LegalNeighboursImpl @Inject()(implicit intToFactory: FactoryIdToFactory) extends LegalNeighbours {
  val memo = new MemoizeScopeToNeighbours() // TODO we could IoC this

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes] = {
    val validForThisScope = memo.apply(key1 = scope, key2 = neighbours)
    validForThisScope.map(intToFactory.convert)
  }
}