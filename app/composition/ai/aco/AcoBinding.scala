package composition.ai.aco

import ai.SelectionStrategy
import ai.aco.Aco
import com.google.inject.AbstractModule

final class AcoBinding extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[SelectionStrategy]).to(classOf[Aco]).asEagerSingleton()
  }
}