package ai.randomWalk

import com.tzavellas.sse.guice.ScalaModule
import ai.{IAi, AiCommon}

class RandomWalkModule extends ScalaModule {
  def configure() {
    bind(classOf[IAi]).to(classOf[RandomWalk]).asEagerSingleton()
  }
}