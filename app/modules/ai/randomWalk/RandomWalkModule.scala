package modules.ai.randomWalk

import com.tzavellas.sse.guice.ScalaModule
import ai.IAi
import ai.randomWalk.RandomGamer

class RandomWalkModule extends ScalaModule {
  def configure() {
    bind(classOf[IAi]).to(classOf[RandomGamer]).asEagerSingleton()
  }
}