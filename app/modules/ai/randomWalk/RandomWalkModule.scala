package modules.ai.randomWalk

import ai.SelectionStrategy
import ai.randomWalk.RandomGamer
import com.tzavellas.sse.guice.ScalaModule

// TODO check name RandomWalk against RandomGamer, which is more correct, also clean up package structure.
final class RandomWalkModule extends ScalaModule {

  def configure(): Unit = {
    bind(classOf[SelectionStrategy]).to(classOf[RandomGamer]).asEagerSingleton()
  }
}