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

  override val factories: Set[PozInt] = Set(AddOperatorFactoryImpl.id, FunctionMFactoryImpl.id, IntegerMFactoryImpl.id, TypeTreeFactoryImpl.id, ObjectFactoryImpl.id, ValDclInFunctionParamFactoryImpl.id, ValueRefFactoryImpl.id)
  override val version: String = factories.mkString("|")

  override def convert(id: PozInt): ReplaceEmpty = id match {
    case AddOperatorFactoryImpl.id => addOperatorFactory
    case FunctionMFactoryImpl.id => functionMFactory
    case IntegerMFactoryImpl.id => integerMFactory
    case TypeTreeFactoryImpl.id => typeTreeFactory
    case ObjectFactoryImpl.id => objectFactory
    case ValDclInFunctionParamFactoryImpl.id => valDclInFunctionParamFactory
    case ValueRefFactoryImpl.id => valueRefFactory
    case _ => throw new RuntimeException(s"Unknown id for factory ${id.value}")
  }

  override def convert(factory: ReplaceEmpty): PozInt = factory match {
    case `addOperatorFactory` => AddOperatorFactoryImpl.id
    case `functionMFactory` => FunctionMFactoryImpl.id
    case `integerMFactory` => IntegerMFactoryImpl.id
    case `typeTreeFactory` => TypeTreeFactoryImpl.id
    case `objectFactory` => ObjectFactoryImpl.id
    case `valDclInFunctionParamFactory` => ValDclInFunctionParamFactoryImpl.id
    case `valueRefFactory` => ValueRefFactoryImpl.id
    case _ => throw new RuntimeException("Unknown factory for id")
  }
}