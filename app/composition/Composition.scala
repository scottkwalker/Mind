package composition

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.TypeLiteral
import composition.ai.legalGamer.LegalGamerBinding
import memoization.Memoize2
import memoization.RepositoryReturningBool
import models.common.IScope
import models.domain.scala.Empty
import play.filters.gzip.GzipFilter
import utils.PozInt

trait Composition extends IoC {

  lazy val injector = Guice.createInjector(
    new DevModule,
    new DecisionBindings,
    new IntegerMBinding,
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
      bind(classOf[Empty])
      bind(new TypeLiteral[Memoize2[IScope, PozInt, Boolean]]() {}).to(classOf[RepositoryReturningBool]).asEagerSingleton()
    }
  }

}