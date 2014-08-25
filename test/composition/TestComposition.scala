package composition

import akka.util.Timeout
import com.google.inject.util.Modules
import com.google.inject.{Guice, Module}
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import scala.concurrent.duration.{FiniteDuration, SECONDS}

abstract class TestComposition extends PlaySpec with MockitoSugar with ScalaFutures with Composition {

  protected implicit val timeout = Timeout(FiniteDuration(1, SECONDS))

  def testInjector(module: Module*) = Guice.createInjector(testModule(module: _*))

  def testModule(module: Module*) = Modules.`override`(new DevModule, new LegalGamerModule).`with`(module: _*)
}