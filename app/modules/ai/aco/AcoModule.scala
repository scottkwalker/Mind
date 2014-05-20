package modules.ai.aco

import com.tzavellas.sse.guice.ScalaModule
import ai.IAi
import ai.aco.Aco

final class AcoModule extends ScalaModule {
  def configure(): Unit = {
    bind(classOf[IAi]).to(classOf[Aco]).asEagerSingleton()
  }
}