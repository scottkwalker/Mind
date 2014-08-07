package composition

import com.google.inject.util.Modules
import com.google.inject.{Guice, Injector, Module}
import modules.DevModule

trait TestComposition extends Composition {
  override lazy val injector: Injector = Guice.createInjector(new DevModule)

  def testModule(module: Module*) = Modules.`override`(new DevModule).`with`(module: _*)
  def testInjector(module: Module*) = Guice.createInjector(testModule(module: _*))
}