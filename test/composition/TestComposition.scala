package composition

import akka.util.Timeout
import com.google.inject.util.Modules.`override`
import com.google.inject.{Guice, Module}
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import scala.concurrent.duration.{FiniteDuration, SECONDS}

abstract class TestComposition extends PlaySpec with MockitoSugar with ScalaFutures {

  protected implicit val timeout = Timeout(FiniteDuration(1, SECONDS))
  private val defaultModules = Seq(new DevModule, new LegalGamerModule)

  def testInjector(modules: Module*) = {
    // The modules override in such a way that if you declare the same binding twice, the last write wins.
    val overridenModules = `override`(defaultModules: _*).`with`(modules: _*)
    Guice.createInjector(overridenModules)
  }
}