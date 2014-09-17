package composition

import ai.RandomNumberGenerator
import com.google.inject.AbstractModule
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}

final class StubRng(rng: RandomNumberGenerator = mock(classOf[RandomNumberGenerator])) extends AbstractModule {

  def configure(): Unit = {
    when(rng.nextInt(any[Int])).thenReturn(2)
    when(rng.nextBoolean).thenReturn(true)
    bind(classOf[RandomNumberGenerator]).toInstance(rng)
  }
}