package ai.randomWalk

import com.tzavellas.sse.guice.ScalaModule
import ai.Ai

class RandomWalkModule extends ScalaModule {
  def configure() {
    bind(classOf[Ai]).to(classOf[RandomWalk]).asEagerSingleton()
  }
}