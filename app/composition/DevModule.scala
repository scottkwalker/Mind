package composition

import _root_.ai.{RandomNumberGenerator, RandomNumberGeneratorImpl}
import com.google.inject.{AbstractModule, TypeLiteral}
import memoization._
import models.common.IScope
import models.domain.scala.{Empty, FactoryLookup, FactoryLookupImpl}
import replaceEmpty._
import utils.PozInt

import scala.concurrent.Future

final class DevModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Empty]).asEagerSingleton()
    bind(classOf[TypeTreeFactory]).to(classOf[TypeTreeFactoryImpl]).asEagerSingleton()
    bind(classOf[ObjectFactory]).to(classOf[ObjectFactoryImpl]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl]).asEagerSingleton()
    bind(classOf[CreateNode]).to(classOf[CreateNodeImpl]).asEagerSingleton()
    bind(classOf[RandomNumberGenerator]).to(classOf[RandomNumberGeneratorImpl]).asEagerSingleton()
    bind(classOf[FactoryLookup]).to(classOf[FactoryLookupImpl]).asEagerSingleton()
    bind(new TypeLiteral[Memoize2[IScope, PozInt, Boolean]]() {}).to(classOf[RepositoryReturningBool]).asEagerSingleton()
    bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl]).asEagerSingleton()
    bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl]).asEagerSingleton()
    bind(classOf[IntegerMFactory]).to(classOf[IntegerMFactoryImpl]).asEagerSingleton()
    bind(classOf[ValDclInFunctionParamFactory]).to(classOf[ValDclInFunctionParamFactoryImpl]).asEagerSingleton()
  }
}