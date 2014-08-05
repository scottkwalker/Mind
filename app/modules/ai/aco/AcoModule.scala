package modules.ai.aco

import ai.SelectionStrategy
import ai.aco.Aco
import com.tzavellas.sse.guice.ScalaModule

final class AcoModule extends ScalaModule {

  def configure(): Unit = {
    bind(classOf[SelectionStrategy]).to(classOf[Aco]).asEagerSingleton()
  }
}