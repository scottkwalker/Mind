package nodes.helpers

import com.tzavellas.sse.guice.ScalaModule
import nodes._
import scala.util.Random

class DevModule extends ScalaModule {
  def configure() {
    bind(classOf[AddOperatorFactory]).asEagerSingleton()
    bind(classOf[Empty]).asEagerSingleton()
    bind(classOf[FunctionMFactory]).asEagerSingleton()
    bind(classOf[NodeTreeFactory]).asEagerSingleton()
    bind(classOf[ObjectMFactory]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).asEagerSingleton()
    bind(classOf[ValueInFunctionParamFactory]).asEagerSingleton()
    bind(classOf[Scope]).toInstance(Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1))
    bind(classOf[CreateNode]).asEagerSingleton()
    bind(classOf[CreateSeqNodes]).asEagerSingleton()
    bind(classOf[Random]).asEagerSingleton()

    //val injector: Injector =  Guice.createInjector(new DevModule)
    //val component = injector.getInstance(classOf[AddOperatorFactory])
  }
}