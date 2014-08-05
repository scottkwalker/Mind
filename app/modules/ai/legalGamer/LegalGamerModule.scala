package modules.ai.legalGamer

import ai.SelectionStrategy
import ai.legalGamer.LegalGamer
import com.tzavellas.sse.guice.ScalaModule

final class LegalGamerModule extends ScalaModule {

  def configure(): Unit = {
    bind(classOf[SelectionStrategy]).to(classOf[LegalGamer]).asEagerSingleton()
  }
}