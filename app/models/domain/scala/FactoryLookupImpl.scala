package models.domain.scala

import com.google.inject.Inject
import com.google.inject.Injector
import decision._
import utils.PozInt

final class FactoryLookupImpl @Inject() (injector: Injector) extends FactoryLookup {

  override val factories: Set[PozInt] = Set(AddOperatorFactory.id, FunctionMFactory.id, IntegerMFactory.id, TypeTreeFactory.id, ObjectFactory.id, ValDclInFunctionParamFactory.id, ValueRefFactory.id)
  override val version: String = factories.mkString("|")

  override def convert(id: PozInt): Decision = id match {
    case AddOperatorFactory.id => injector.getInstance(classOf[AddOperatorFactory])
    case FunctionMFactory.id => injector.getInstance(classOf[FunctionMFactory])
    case IntegerMFactory.id => injector.getInstance(classOf[IntegerMFactory])
    case TypeTreeFactory.id => injector.getInstance(classOf[TypeTreeFactory])
    case ObjectFactory.id => injector.getInstance(classOf[ObjectFactory])
    case ValDclInFunctionParamFactory.id => injector.getInstance(classOf[ValDclInFunctionParamFactory])
    case ValueRefFactory.id => injector.getInstance(classOf[ValueRefFactory])
    case _ => throw new RuntimeException(s"Unknown id for factory ${id.value}")
  }

  override def convert(factory: Decision): PozInt = factory match {
    case _: AddOperatorFactory => AddOperatorFactory.id
    case _: FunctionMFactory => FunctionMFactory.id
    case _: IntegerMFactory => IntegerMFactory.id
    case _: TypeTreeFactory => TypeTreeFactory.id
    case _: ObjectFactory => ObjectFactory.id
    case _: ValDclInFunctionParamFactory => ValDclInFunctionParamFactory.id
    case _: ValueRefFactory => ValueRefFactory.id
    case _ => throw new RuntimeException("Unknown factory for id")
  }
}