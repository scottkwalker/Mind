package composition

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.TypeLiteral
import com.google.inject.util.Modules.`override`
import decision._
import memoization.Memoize2
import memoization.RepositoryReturningBool
import models.common.IScope
import models.domain.scala.Empty
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import utils.PozInt

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.SECONDS

trait TestComposition extends PlaySpec with MockitoSugar with ScalaFutures with Eventually {

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

  private def defaultModules = Seq(
    new TestModule,
    new StubCreateNodeBinding,
    new StubCreateSeqNodesBinding,
    new StubFactoryLookupBinding,
    new StubGeneratorBinding,
    new StubLookupChildrenBinding,
    new StubLookupChildrenWithFutures,
    new StubReplaceEmptyBinding,
    new StubRepositoryBinding,
    new StubRepositoryWithFuture,
    new StubRngBinding,
    new StubSelectionStrategyBinding
  )

  class TestModule extends AbstractModule {

    override def configure(): Unit = {
      bind(classOf[Empty]).asEagerSingleton()
      bind(classOf[TypeTreeFactory]).to(classOf[TypeTreeFactoryImpl]).asEagerSingleton()
      bind(classOf[ObjectFactory]).to(classOf[ObjectFactoryImpl]).asEagerSingleton()
      bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl]).asEagerSingleton()
      bind(new TypeLiteral[Memoize2[IScope, PozInt, Boolean]]() {}).to(classOf[RepositoryReturningBool]).asEagerSingleton()
      bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl]).asEagerSingleton()
      bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl]).asEagerSingleton()
      bind(classOf[IntegerMFactory]).to(classOf[IntegerMFactoryImpl]).asEagerSingleton()
      bind(classOf[ValDclInFunctionParamFactory]).to(classOf[ValDclInFunctionParamFactoryImpl]).asEagerSingleton()
    }
  }

}