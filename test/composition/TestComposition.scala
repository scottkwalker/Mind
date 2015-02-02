package composition

import com.google.inject.util.Modules.`override`
import com.google.inject.{Guice, Module}
import composition.ai.legalGamer.LegalGamerModule
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Milliseconds, Seconds, Span}
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration.{FiniteDuration, SECONDS}

trait TestComposition extends PlaySpec with MockitoSugar with ScalaFutures {

  protected val finiteTimeout = FiniteDuration(2, SECONDS)
  protected implicit val timeout = akka.util.Timeout(duration = finiteTimeout)
  private val defaultModules = Seq(new DevModule, new LegalGamerModule)
  protected val whenReadyPatienceConfig = {
    val timeout = org.scalatest.time.Span(2, Seconds)
    val interval = org.scalatest.time.Span(15, Milliseconds)
    PatienceConfig(timeout, interval)
  }

  def testInjector(modules: Module*) = {
    // The modules override in such a way that if you declare the same binding twice, the last write wins.
    val overridenModules = `override`(defaultModules: _*).`with`(modules: _*)
    Guice.createInjector(overridenModules)
  }
}