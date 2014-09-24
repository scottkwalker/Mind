package composition

import ai.RandomNumberGenerator
import com.google.inject.AbstractModule
import org.mockito.Mockito.mock

final class StubRng(rng: RandomNumberGenerator = mock(classOf[RandomNumberGenerator])) extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[RandomNumberGenerator]).toInstance(rng)
  }
}