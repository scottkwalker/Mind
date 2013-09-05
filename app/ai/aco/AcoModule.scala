package ai.aco

import com.tzavellas.sse.guice.ScalaModule
import ai.Ai

class AcoModule extends ScalaModule {
  def configure() {
    bind(classOf[Ai]).to(classOf[Aco]).asEagerSingleton()
  }
}