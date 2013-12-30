package nodes.helpers

import com.tzavellas.sse.guice.ScalaModule
import nodes._
import scala.util.Random
import ai.{RandomNumberGenerator, IRandomNumberGenerator}

class DevModule extends ScalaModule {
  def configure() {
    bind(classOf[AddOperatorFactory]).asEagerSingleton()
    bind(classOf[Empty]).asEagerSingleton()
    bind(classOf[FunctionMFactory]).asEagerSingleton()
    bind(classOf[NodeTreeFactory]).asEagerSingleton()
    bind(classOf[ObjectDefFactory]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).asEagerSingleton()
    bind(classOf[ValDclInFunctionParamFactory]).asEagerSingleton()
    bind(classOf[IScope]).toInstance(Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1))
    bind(classOf[CreateNode]).asEagerSingleton()
    bind(classOf[CreateSeqNodes]).asEagerSingleton()
    bind(classOf[IRandomNumberGenerator]).toInstance(RandomNumberGenerator())
    bind(classOf[MemoizeDi]).toInstance(MemoizeDi())
    //val injector: Injector =  Guice.createInjector(new DevModule)
    //val component = injector.getInstance(classOf[AddOperatorFactory])
  }
}