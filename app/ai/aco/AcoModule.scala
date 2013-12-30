package ai.aco

import com.tzavellas.sse.guice.ScalaModule
import ai.{IAi, Ai}

class AcoModule extends ScalaModule {
  def configure() {
    bind(classOf[IAi]).to(classOf[Aco]).asEagerSingleton()
  }
}