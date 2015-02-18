package composition

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.util.Modules.`override`

trait TestComposition extends Composition {

  override lazy val injector: Injector = testInjector()

  protected def testInjector(modules: Module*) = {
    // The modules override in such a way that if you declare the same binding twice, the last write wins.
    val overridenModules = `override`(defaultModules: _*).`with`(modules: _*)
    Guice.createInjector(overridenModules)
  }

  // Modules that stub most of the application's dependencies. This should be enough for most UI tests.
  private def defaultModules = Seq(
    new StubCreateNodeBinding,
    new StubCreateSeqNodesBinding,
    //    new StubLookupChildrenWithFutures,
    new StubSelectionStrategyBinding
  )
}