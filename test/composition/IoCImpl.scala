package composition

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import decision._
import models.domain.scala.Empty

trait IoCImpl extends IoC {

  override lazy val injector: Injector = Guice.createInjector(defaultModules: _*)

  // Modules that stub most of the application's dependencies. This should be enough for most UI tests.
  def defaultModules = Seq(
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
    new StubRepositoryWithFuture,
    new StubRngBinding,
    new StubSelectionStrategyBinding
  )

  private final class TestModule extends AbstractModule {

    override def configure(): Unit = {
      bind(classOf[Empty]).asEagerSingleton()
      bind(classOf[TypeTreeFactory]).to(classOf[TypeTreeFactoryImpl]).asEagerSingleton()
      bind(classOf[ObjectFactory]).to(classOf[ObjectFactoryImpl]).asEagerSingleton()
      bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl]).asEagerSingleton()
      bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl]).asEagerSingleton()
      bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl]).asEagerSingleton()
      bind(classOf[IntegerMFactory]).to(classOf[IntegerMFactoryImpl]).asEagerSingleton()
      bind(classOf[ValDclInFunctionParamFactory]).to(classOf[ValDclInFunctionParamFactoryImpl]).asEagerSingleton()
    }
  }

}
