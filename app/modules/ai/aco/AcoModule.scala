package modules.ai.aco

import com.tzavellas.sse.guice.ScalaModule
import ai.IAi
import ai.aco.Aco

class AcoModule extends ScalaModule {
  def configure() {
    bind(classOf[IAi]).to(classOf[Aco]).asEagerSingleton()
  }
}