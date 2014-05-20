package modules.ai.legalGamer

import com.tzavellas.sse.guice.ScalaModule
import ai.IAi
import ai.legalGamer.LegalGamer

final class LegalGamerModule extends ScalaModule {
  def configure(): Unit = {
    bind(classOf[IAi]).to(classOf[LegalGamer]).asEagerSingleton()
  }
}