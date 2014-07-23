package modules.ai.aco

import ai.IAi
import ai.aco.Aco
import com.tzavellas.sse.guice.ScalaModule

final class AcoModule extends ScalaModule {

  def configure(): Unit = {
    bind(classOf[IAi]).to(classOf[Aco]).asEagerSingleton()
  }
}