package composition

import _root_.ai.RandomNumberGenerator
import com.google.inject.AbstractModule
import org.mockito.Mockito.mock

final class StubRng(randomNumberGenerator: RandomNumberGenerator = mock(classOf[RandomNumberGenerator])) extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[RandomNumberGenerator]).toInstance(randomNumberGenerator)
  }
}