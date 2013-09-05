package ai.aco

import com.tzavellas.sse.guice.ScalaModule
import nodes._
import com.google.inject.Guice
import com.google.inject.Injector
import ai.Ai

class AcoModule extends ScalaModule {
  def configure() {
    bind(classOf[Ai]).to(classOf[Aco]).asEagerSingleton()
  }
}