package models.domain.scala

import com.google.inject.Inject
import replaceEmpty._
import utils.PozInt

final class FactoryLookupImpl @Inject()(addOperatorFactory: AddOperatorFactory,
                                        functionMFactory: FunctionMFactory,
                                        integerMFactory: IntegerMFactory,
                                        typeTreeFactory: TypeTreeFactory,
                                        objectFactory: ObjectFactory,
                                        valDclInFunctionParamFactory: ValDclInFunctionParamFactory,
                                        valueRefFactory: ValueRefFactory) extends FactoryLookup {

  override val factories: Set[PozInt] = Set(AddOperatorFactory.id, FunctionMFactory.id, IntegerMFactoryImpl.id, TypeTreeFactory.id, ObjectFactory.id, ValDclInFunctionParamFactoryImpl.id, ValueRefFactoryImpl.id)
  override val version: String = factories.mkString("|")

  override def convert(id: PozInt): ReplaceEmpty = id match {
    case AddOperatorFactory.id => addOperatorFactory
    case FunctionMFactory.id => functionMFactory
    case IntegerMFactoryImpl.id => integerMFactory
    case TypeTreeFactory.id => typeTreeFactory
    case ObjectFactory.id => objectFactory
    case ValDclInFunctionParamFactoryImpl.id => valDclInFunctionParamFactory
    case ValueRefFactoryImpl.id => valueRefFactory
    case _ => throw new RuntimeException(s"Unknown id for factory ${id.value}")
  }

  override def convert(factory: ReplaceEmpty): PozInt = factory match {
    case `addOperatorFactory` => AddOperatorFactory.id
    case `functionMFactory` => FunctionMFactory.id
    case `integerMFactory` => IntegerMFactoryImpl.id
    case `typeTreeFactory` => TypeTreeFactory.id
    case `objectFactory` => ObjectFactory.id
    case `valDclInFunctionParamFactory` => ValDclInFunctionParamFactoryImpl.id
    case `valueRefFactory` => ValueRefFactoryImpl.id
    case _ => throw new RuntimeException("Unknown factory for id")
  }
}