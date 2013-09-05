package ai.helpers

import com.tzavellas.sse.guice.ScalaModule
import ai.Ai

class TestAiModule extends ScalaModule {
  def configure() {
    bind(classOf[Ai]).to(classOf[TestAi]).asEagerSingleton()
  }
}