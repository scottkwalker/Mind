package composition

import com.google.inject.AbstractModule
import memoization.Generator
import memoization.GeneratorImpl

final class GeneratorBinding extends AbstractModule {

  override def configure(): Unit = bind(classOf[Generator]).to(classOf[GeneratorImpl]).asEagerSingleton()
}