package nodes.legalNeighbours

import com.google.inject.Inject
import models.common.IScope
import nodes._
import nodes.helpers.ICreateChildNodes
import nodes.memoization.MemoizeScopeToNeighbours

final class LegalNeighboursImpl @Inject()(implicit factoryIdToFactory: FactoryIdToFactory) extends LegalNeighbours {

  private val versioning = s"${AddOperatorFactoryImpl.id}|${FunctionMFactoryImpl.id}|${IntegerMFactoryImpl.id}|${NodeTreeFactoryImpl.id}|${ObjectDefFactoryImpl.id}|${ValDclInFunctionParamFactoryImpl.id}|${ValueRefFactoryImpl.id}"
  private val memo = new MemoizeScopeToNeighbours(versioning = versioning, factoryIdToFactory = factoryIdToFactory)

  // TODO we could IoC this
  /*    case AddOperatorFactoryImpl.id => addOperatorFactory
      case FunctionMFactoryImpl.id => functionMFactory
      case IntegerMFactory.id => integerMFactory
      case NodeTreeFactoryImpl.id => nodeTreeFactory
      case ObjectDefFactoryImpl.id => objectDefFactory
      case ValDclInFunctionParamFactoryImpl.id => valDclInFunctionParamFactory
      case ValueRefFactoryImpl.id => valueRefFactory*/
  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes] = {
    neighbours.
      filter(neighbour => memo.apply(key1 = scope, key2 = neighbour)). // Remove neighbours that cannot terminate at this scope.
      map(factoryIdToFactory.convert)
  }
}