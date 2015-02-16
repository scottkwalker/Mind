package composition

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.TypeLiteral
import composition.ai.legalGamer.LegalGamerBinding
import decision.IntegerMFactory
import decision.IntegerMFactoryImpl
import decision.ValDclInFunctionParamFactory
import decision.ValDclInFunctionParamFactoryImpl
import decision._
import memoization.Memoize2
import memoization.RepositoryReturningBool
import models.common.IScope
import models.domain.scala.Empty
import models.domain.scala.FactoryLookup
import models.domain.scala.FactoryLookupImpl
import play.filters.gzip.GzipFilter
import utils.PozInt

trait Composition extends IoC {

  lazy val injector = Guice.createInjector(
    new DevModule,
    new CreateNodeBinding,
    new CreateSeqNodesBinding,
    new FactoryLookupBinding,
    new GeneratorBinding,
    new LegalGamerBinding,
    new LookupChildrenBinding,
    new LookupChildrenWithFuturesBinding,
    new RandomNumberGeneratorBinding,
    new RepositoryBinding,
    new RepositoryWithFuturesBinding
  )

  lazy val filters = Array(
    new GzipFilter()
  )

  final class DevModule extends AbstractModule {

    override def configure(): Unit = {
      bind(classOf[Empty]).asEagerSingleton()
      bind(classOf[TypeTreeFactory]).to(classOf[TypeTreeFactoryImpl]).asEagerSingleton()
      bind(classOf[ObjectFactory]).to(classOf[ObjectFactoryImpl]).asEagerSingleton()
      bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl]).asEagerSingleton()
      bind(classOf[FactoryLookup]).to(classOf[FactoryLookupImpl]).asEagerSingleton()
      bind(new TypeLiteral[Memoize2[IScope, PozInt, Boolean]]() {}).to(classOf[RepositoryReturningBool]).asEagerSingleton()
      bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl]).asEagerSingleton()
      bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl]).asEagerSingleton()
      bind(classOf[IntegerMFactory]).to(classOf[IntegerMFactoryImpl]).asEagerSingleton()
      bind(classOf[ValDclInFunctionParamFactory]).to(classOf[ValDclInFunctionParamFactoryImpl]).asEagerSingleton()
    }
  }

}