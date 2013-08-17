package nodes.helpers

import com.tzavellas.sse.guice.ScalaModule
import nodes._

class DevModule extends ScalaModule {
  def configure() {
    bind[AddOperatorFactory].to[AddOperatorFactory]
    /*bind[Empty].to[Empty]
    bind[FunctionM].to[FunctionM]
    bind[NodeTree].to[NodeTree]
    bind[ObjectM].to[ObjectM]*/
    bind[ValueRefFactory].to[ValueRefFactory]
    
    //val injector =  Guice.createInjector(new DevModule)
    //val aa = injector.getInstance(classOf[AddOperator])
  }
}