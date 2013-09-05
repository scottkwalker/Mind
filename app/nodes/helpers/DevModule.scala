package nodes.helpers

import com.tzavellas.sse.guice.ScalaModule
import nodes._
import com.google.inject.Guice
import com.google.inject.Injector
import ai.Ai
import ai.aco.Aco
import com.google.inject.name.Names

class DevModule extends ScalaModule {
  def configure() {
    bind(classOf[AddOperatorFactory]).asEagerSingleton()
    bind(classOf[Empty]).asEagerSingleton()
    bind(classOf[FunctionMFactory]).asEagerSingleton()
    bind(classOf[NodeTreeFactory]).asEagerSingleton()
    bind(classOf[ObjectMFactory]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).asEagerSingleton()
    bind(classOf[Scope]).toInstance(Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10))
    bind(classOf[CreateNode]).asEagerSingleton()
    bind(classOf[CreateSeqNodes]).asEagerSingleton()
    
    //val injector: Injector =  Guice.createInjector(new DevModule)
    //val component = injector.getInstance(classOf[AddOperatorFactory])
  }
}