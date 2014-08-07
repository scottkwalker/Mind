package composition

import com.google.inject.Guice
import modules.DevModule
import play.filters.gzip.GzipFilter

trait Composition {
  lazy val injector = Guice.createInjector(new DevModule)

  lazy val filters = Array(
    new GzipFilter()
  )
}