package composition.ai.legalGamer

import ai.SelectionStrategy
import ai.legalGamer.LegalGamer
import com.google.inject.AbstractModule

final class LegalGamerBinding extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[SelectionStrategy]).to(classOf[LegalGamer]).asEagerSingleton()
  }
}