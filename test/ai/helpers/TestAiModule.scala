package ai.helpers

import com.tzavellas.sse.guice.ScalaModule
import nodes._
import com.google.inject.Guice
import com.google.inject.Injector
import ai.Ai

class TestAiModule extends ScalaModule {
  def configure() {
    bind(classOf[Ai]).to(classOf[TestAi]).asEagerSingleton()
  }
}