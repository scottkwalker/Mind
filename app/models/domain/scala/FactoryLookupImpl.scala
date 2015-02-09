package models.domain.scala

import com.google.inject.Inject
import decision._
import utils.PozInt

final class FactoryLookupImpl @Inject()(addOperatorFactory: AddOperatorFactory,
                                        functionMFactory: FunctionMFactory,
                                        integerMFactory: IntegerMFactory,
                                        typeTreeFactory: TypeTreeFactory,
                                        objectFactory: ObjectFactory,
                                        valDclInFunctionParamFactory: ValDclInFunctionParamFactory,
                                        valueRefFactory: ValueRefFactory) extends FactoryLookup {

  override val factories: Set[PozInt] = Set(AddOperatorFactory.id, FunctionMFactory.id, IntegerMFactory.id, TypeTreeFactory.id, ObjectFactory.id, ValDclInFunctionParamFactory.id, ValueRefFactory.id)
  override val version: String = factories.mkString("|")

  override def convert(id: PozInt): Decision = id match {
    case AddOperatorFactory.id => addOperatorFactory
    case FunctionMFactory.id => functionMFactory
    case IntegerMFactory.id => integerMFactory
    case TypeTreeFactory.id => typeTreeFactory
    case ObjectFactory.id => objectFactory
    case ValDclInFunctionParamFactory.id => valDclInFunctionParamFactory
    case ValueRefFactory.id => valueRefFactory
    case _ => throw new RuntimeException(s"Unknown id for factory ${id.value}")
  }

  override def convert(factory: Decision): PozInt = factory match {
    case `addOperatorFactory` => AddOperatorFactory.id
    case `functionMFactory` => FunctionMFactory.id
    case `integerMFactory` => IntegerMFactory.id
    case `typeTreeFactory` => TypeTreeFactory.id
    case `objectFactory` => ObjectFactory.id
    case `valDclInFunctionParamFactory` => ValDclInFunctionParamFactory.id
    case `valueRefFactory` => ValueRefFactory.id
    case _ => throw new RuntimeException("Unknown factory for id")
  }
}