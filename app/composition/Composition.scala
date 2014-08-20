package composition

import com.google.inject.Guice
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import play.filters.gzip.GzipFilter

trait Composition {

  lazy val injector = Guice.createInjector(new DevModule, new LegalGamerModule)

  lazy val filters = Array(
    new GzipFilter()
  )
}