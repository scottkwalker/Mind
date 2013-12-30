package ai.helpers

import com.tzavellas.sse.guice.ScalaModule
import ai.IAi

class TestAiModule extends ScalaModule {
  def configure() {
    bind(classOf[IAi]).to(classOf[TestAiCommon]).asEagerSingleton()
  }
}