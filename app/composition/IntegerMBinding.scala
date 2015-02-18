package composition

import com.google.inject.AbstractModule
import decision.IntegerMFactory
import decision.IntegerMFactoryImpl

final class IntegerMBinding extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[IntegerMFactory]).to(classOf[IntegerMFactoryImpl])
  }
}
