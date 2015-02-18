package composition

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.util.Modules.`override`
import decision._
import models.domain.scala.Empty

trait TestComposition extends Composition {

  override lazy val injector: Injector = testInjector()

  protected def testInjector(modules: Module*) = {
    // The modules override in such a way that if you declare the same binding twice, the last write wins.
    val overridenModules = `override`(defaultModules: _*).`with`(modules: _*)
    Guice.createInjector(overridenModules)
  }

  // Modules that stub most of the application's dependencies. This should be enough for most UI tests.
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
    new StubRepositoryReturningBoolBinding,
//    new StubRepositoryWithFuture,
    new StubSelectionStrategyBinding
  )

  private final class TestModule extends AbstractModule {

    override def configure(): Unit = {
      bind(classOf[Empty])
      bind(classOf[TypeTreeFactory]).to(classOf[TypeTreeFactoryImpl])
      bind(classOf[ObjectFactory]).to(classOf[ObjectFactoryImpl])
      bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl])
      bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl])
      bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl])
      bind(classOf[IntegerMFactory]).to(classOf[IntegerMFactoryImpl])
      bind(classOf[ValDclInFunctionParamFactory]).to(classOf[ValDclInFunctionParamFactoryImpl])
    }
  }

}
