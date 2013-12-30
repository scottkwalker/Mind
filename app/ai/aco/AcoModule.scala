package ai.aco

import com.tzavellas.sse.guice.ScalaModule
import ai.IAi

class AcoModule extends ScalaModule {
  def configure() {
    bind(classOf[IAi]).to(classOf[Aco]).asEagerSingleton()
  }
}