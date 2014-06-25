package modules.ai.randomWalk

import ai.IAi
import ai.randomWalk.RandomGamer
import com.tzavellas.sse.guice.ScalaModule

final class RandomWalkModule extends ScalaModule {
  def configure(): Unit = {
    bind(classOf[IAi]).to(classOf[RandomGamer]).asEagerSingleton()
  }
}