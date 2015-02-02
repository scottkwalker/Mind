package composition

import com.google.inject.util.Modules.`override`
import com.google.inject.{Guice, Module}
import composition.ai.legalGamer.LegalGamerModule
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Milliseconds, Seconds, Span}
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration.{FiniteDuration, SECONDS}

trait TestComposition extends PlaySpec with MockitoSugar with ScalaFutures with Eventually {

  protected val finiteTimeout = FiniteDuration(2, SECONDS)
  protected implicit val timeout = akka.util.Timeout(duration = finiteTimeout)
  private val defaultModules = Seq(new DevModule, new LegalGamerModule)

  def testInjector(modules: Module*) = {
    // The modules override in such a way that if you declare the same binding twice, the last write wins.
    val overridenModules = `override`(defaultModules: _*).`with`(modules: _*)
    Guice.createInjector(overridenModules)
  }
}