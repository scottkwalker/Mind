package composition

import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.util.Modules.`override`
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.SECONDS

trait TestComposition extends PlaySpec with MockitoSugar with ScalaFutures with Eventually with IoCImpl {

  protected val finiteTimeout = FiniteDuration(2, SECONDS)
  protected implicit val timeout = akka.util.Timeout(duration = finiteTimeout)

  // The default PatienceConfig from ScalaTest uses scaled time spans. The interval and timeout for the PatienceConfig
  // will be scaled, allowing us to run the tests with fail-fast values for local testing or high values for testing
  // on Continuous Integration servers (which tend to be much slower).
  // http://doc.scalatest.org/2.2.1/index.html#org.scalatest.concurrent.ScaledTimeSpans
  // We will read spanScaleFactor from system properties (if one exists, otherwise use the default). This system
  // property should be overridden in the SBT command line argument in a build server's build script e.g. .travis.yml
  override def spanScaleFactor: Double = sys.props.getOrElse("spanScaleFactor", "1.0").toDouble

  def testInjector(modules: Module*) = {
    // The modules override in such a way that if you declare the same binding twice, the last write wins.
    val overridenModules = `override`(defaultModules: _*).`with`(modules: _*)
    Guice.createInjector(overridenModules)
  }
}