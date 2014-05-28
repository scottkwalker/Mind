package nodes.legalNeighbours

import nodes._
import com.google.inject.Inject
import nodes.helpers.ICreateChildNodes

final class FactoryIdToFactoryImpl @Inject()(addOperatorFactory: AddOperatorFactory,
                                   functionMFactory: FunctionMFactory,
                                   integerMFactory: IntegerMFactory,
                                   nodeTreeFactory: NodeTreeFactory,
                                   objectDefFactory: ObjectDefFactory,
                                   valDclInFunctionParamFactory: ValDclInFunctionParamFactory,
                                   valueRefFactory: ValueRefFactory) extends FactoryIdToFactory {
  override def convert(id: Int): ICreateChildNodes = id match {
    case AddOperatorFactory.id => addOperatorFactory
    case FunctionMFactory.id => functionMFactory
    case IntegerMFactory.id => integerMFactory
    case NodeTreeFactory.id => nodeTreeFactory
    case ObjectDefFactory.id => objectDefFactory
    case ValDclInFunctionParamFactory.id => valDclInFunctionParamFactory
    case ValueRefFactory.id => valueRefFactory
    case _ => throw new RuntimeException("Unknown id for factory")
  }
}
