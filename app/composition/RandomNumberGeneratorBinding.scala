package composition

import _root_.ai.RandomNumberGenerator
import _root_.ai.RandomNumberGeneratorImpl
import com.google.inject.AbstractModule

final class RandomNumberGeneratorBinding extends AbstractModule {

  override def configure(): Unit = bind(classOf[RandomNumberGenerator]).to(classOf[RandomNumberGeneratorImpl]).asEagerSingleton()
}