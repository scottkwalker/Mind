package composition

import com.google.inject.util.Modules
import com.google.inject.{Guice, Module}
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule

trait TestComposition extends Composition {

  def testModule(module: Module*) = Modules.`override`(new DevModule, new LegalGamerModule).`with`(module: _*)

  def testInjector(module: Module*) = Guice.createInjector(testModule(module: _*))
}