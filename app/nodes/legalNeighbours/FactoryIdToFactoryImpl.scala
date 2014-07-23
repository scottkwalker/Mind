package nodes.legalNeighbours

import com.google.inject.Inject
import nodes._
import nodes.helpers.ICreateChildNodes

final class FactoryIdToFactoryImpl @Inject()(addOperatorFactory: AddOperatorFactory,
                                             functionMFactory: FunctionMFactory,
                                             integerMFactory: IntegerMFactoryImpl,
                                             nodeTreeFactory: NodeTreeFactory,
                                             objectDefFactory: ObjectDefFactory,
                                             valDclInFunctionParamFactory: ValDclInFunctionParamFactory,
                                             valueRefFactory: ValueRefFactory) extends FactoryIdToFactory {
  override def convert(id: Int): ICreateChildNodes = id match {
    case AddOperatorFactoryImpl.id => addOperatorFactory
    case FunctionMFactoryImpl.id => functionMFactory
    case IntegerMFactoryImpl.id => integerMFactory
    case NodeTreeFactoryImpl.id => nodeTreeFactory
    case ObjectDefFactoryImpl.id => objectDefFactory
    case ValDclInFunctionParamFactoryImpl.id => valDclInFunctionParamFactory
    case ValueRefFactoryImpl.id => valueRefFactory
    case _ => throw new RuntimeException("Unknown id for factory")
  }
}
