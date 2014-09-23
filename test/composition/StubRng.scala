package composition

import ai.RandomNumberGenerator
import com.google.inject.AbstractModule
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}

final class StubRng(rng: RandomNumberGenerator = mock(classOf[RandomNumberGenerator])) extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[RandomNumberGenerator]).toInstance(rng)
  }
}