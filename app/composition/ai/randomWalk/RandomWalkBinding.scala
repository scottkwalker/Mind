package composition.ai.randomWalk

import ai.SelectionStrategy
import ai.randomWalk.RandomGamer
import com.google.inject.AbstractModule

// TODO check name RandomWalk against RandomGamer, which is more correct, also clean up package structure.
final class RandomWalkBinding extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[SelectionStrategy]).to(classOf[RandomGamer]).asEagerSingleton()
  }
}
