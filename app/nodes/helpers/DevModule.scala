package nodes.helpers

import com.tzavellas.sse.guice.ScalaModule
import nodes._
import com.google.inject.Guice
import com.google.inject.Injector

class DevModule extends ScalaModule {
  def configure() {
    bind(classOf[AddOperatorFactory])
    bind(classOf[Empty])//.to(classOf[Empty])
    bind(classOf[FunctionMFactory])
    bind(classOf[NodeTreeFactory])
    bind(classOf[ObjectMFactory])
    bind(classOf[ValueRefFactory])
    
    //val injector: Injector =  Guice.createInjector(new DevModule)
    //val component = injector.getInstance(classOf[AddOperatorFactory])
  }
}