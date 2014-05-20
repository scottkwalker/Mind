package modules.ai.randomWalk

import com.tzavellas.sse.guice.ScalaModule
import ai.IAi
import ai.randomWalk.RandomGamer

final class RandomWalkModule extends ScalaModule {
  def configure(): Unit = {
    bind(classOf[IAi]).to(classOf[RandomGamer]).asEagerSingleton()
  }
}