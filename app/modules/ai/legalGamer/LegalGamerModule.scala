package modules.ai.legalGamer

import com.tzavellas.sse.guice.ScalaModule
import ai.IAi
import ai.legalGamer.LegalGamer

class LegalGamerModule extends ScalaModule {
  def configure() {
    bind(classOf[IAi]).to(classOf[LegalGamer]).asEagerSingleton()
  }
}