package memoization

import com.google.inject.Inject
import replaceEmpty._

final class FactoryLookupImpl @Inject()(addOperatorFactory: AddOperatorFactory,
                                        functionMFactory: FunctionMFactory,
                                        integerMFactory: IntegerMFactory,
                                        nodeTreeFactory: NodeTreeFactory,
                                        objectDefFactory: ObjectDefFactory,
                                        valDclInFunctionParamFactory: ValDclInFunctionParamFactory,
                                        valueRefFactory: ValueRefFactory) extends FactoryLookup {

  override val version: String = s"${AddOperatorFactoryImpl.id}|${FunctionMFactoryImpl.id}|${IntegerMFactoryImpl.id}|${NodeTreeFactoryImpl.id}|${ObjectDefFactoryImpl.id}|${ValDclInFunctionParamFactoryImpl.id}|${ValueRefFactoryImpl.id}"

  override def convert(id: Int): ReplaceEmpty = id match {
    case AddOperatorFactoryImpl.id => addOperatorFactory
    case FunctionMFactoryImpl.id => functionMFactory
    case IntegerMFactoryImpl.id => integerMFactory
    case NodeTreeFactoryImpl.id => nodeTreeFactory
    case ObjectDefFactoryImpl.id => objectDefFactory
    case ValDclInFunctionParamFactoryImpl.id => valDclInFunctionParamFactory
    case ValueRefFactoryImpl.id => valueRefFactory
    case _ => throw new RuntimeException("Unknown id for factory")
  }

  override def convert(factory: ReplaceEmpty): Int = factory match {
    case `addOperatorFactory` => AddOperatorFactoryImpl.id
    case `functionMFactory` => FunctionMFactoryImpl.id
    case `integerMFactory` => IntegerMFactoryImpl.id
    case `nodeTreeFactory` => NodeTreeFactoryImpl.id
    case `objectDefFactory` => ObjectDefFactoryImpl.id
    case `valDclInFunctionParamFactory` => ValDclInFunctionParamFactoryImpl.id
    case `valueRefFactory` => ValueRefFactoryImpl.id
    case _ => throw new RuntimeException("Unknown factory for id")
  }
}
