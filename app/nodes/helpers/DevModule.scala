package nodes.helpers

import com.tzavellas.sse.guice.ScalaModule
import nodes._

class DevModule extends ScalaModule {
  def configure() {
    bind(classOf[AddOperatorFactory])
    bind(classOf[Empty]).to(classOf[Empty])
    /*bind(classOf[FunctionM]).to(classOf[FunctionM])
    bind(classOf[NodeTree]).to(classOf[NodeTree])
    bind(classOf[ObjectM]).to(classOf[ObjectM])*/
    bind(classOf[ValueRefFactory])
    
    //val injector =  Guice.createInjector(new DevModule)
    //val aa = injector.getInstance(classOf[AddOperator])
  }
}