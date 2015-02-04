package composition

import _root_.ai.{RandomNumberGenerator, RandomNumberGeneratorImpl}
import com.google.inject.util.Modules.`override`
import com.google.inject.{AbstractModule, Guice, Module, TypeLiteral}
import memoization._
import models.common.{IScope, Scope}
import models.domain.scala.{Empty, FactoryLookup, FactoryLookupImpl}
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import replaceEmpty._
import utils.PozInt

import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, SECONDS}

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

  private def defaultModules = Seq(
    new TestModule,
    new StubCreateSeqNodesBinding,

    new StubGeneratorBinding,
    new StubLookupChildrenBinding,
    new StubLookupChildrenWithFutures,
    new StubRngBinding,
    new StubSelectionStrategyBinding
  )

  def testInjector(modules: Module*) = {
    // The modules override in such a way that if you declare the same binding twice, the last write wins.
    val overridenModules = `override`(defaultModules: _*).`with`(modules: _*)
    Guice.createInjector(overridenModules)
  }

  class TestModule extends AbstractModule {

    override def configure(): Unit = {
      bind(classOf[Empty]).asEagerSingleton()
      bind(classOf[TypeTreeFactory]).to(classOf[TypeTreeFactoryImpl]).asEagerSingleton()
      bind(classOf[ObjectFactory]).to(classOf[ObjectFactoryImpl]).asEagerSingleton()
      bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl]).asEagerSingleton()
      bind(classOf[IScope]).toInstance(Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1))
      bind(classOf[CreateNode]).to(classOf[CreateNodeImpl]).asEagerSingleton()
      bind(classOf[FactoryLookup]).to(classOf[FactoryLookupImpl]).asEagerSingleton()
      bind(new TypeLiteral[Memoize2[IScope, PozInt, Boolean]]() {}).to(classOf[RepositoryReturningBool]).asEagerSingleton()
      bind(new TypeLiteral[Memoize2[IScope, PozInt, Future[Boolean]]]() {}).to(classOf[RepositoryReturningFutureBool]).asEagerSingleton()
      bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl]).asEagerSingleton()
      bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl]).asEagerSingleton()
      bind(classOf[IntegerMFactory]).to(classOf[IntegerMFactoryImpl]).asEagerSingleton()
      bind(classOf[ValDclInFunctionParamFactory]).to(classOf[ValDclInFunctionParamFactoryImpl]).asEagerSingleton()
      bind(new TypeLiteral[Memoize2WithSet[IScope, PozInt]]() {}).to(classOf[RepositoryWithSetImpl]).asEagerSingleton()
    }
  }

}