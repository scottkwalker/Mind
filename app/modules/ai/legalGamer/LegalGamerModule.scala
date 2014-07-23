package modules.ai.legalGamer

import ai.IAi
import ai.legalGamer.LegalGamer
import com.tzavellas.sse.guice.ScalaModule

final class LegalGamerModule extends ScalaModule {

  def configure(): Unit = {
    bind(classOf[IAi]).to(classOf[LegalGamer]).asEagerSingleton()
  }
}