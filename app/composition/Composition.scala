package composition

import com.google.inject.Guice
import composition.ai.legalGamer.LegalGamerBinding
import play.filters.gzip.GzipFilter

trait Composition {

  lazy val injector = Guice.createInjector(
    new DevModule,
    new LegalGamerBinding,
    new CreateSeqNodesBinding,
    new FactoryLookupBinding,
    new GeneratorBinding,
    new LookupChildrenBinding,
    new LookupChildrenWithFuturesBinding,
    new RepositoryBinding,
    new RepositoryWithFuturesBinding
  )

  lazy val filters = Array(
    new GzipFilter()
  )
}