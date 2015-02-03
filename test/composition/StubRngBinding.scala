package composition

import _root_.ai.RandomNumberGenerator
import com.google.inject.AbstractModule
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar

final class StubRngBinding(nextBoolean: Boolean = true, nextInt: Int = 0) extends AbstractModule with MockitoSugar {

  val stub = {
    val rng = mock[RandomNumberGenerator]
    when(rng.nextBoolean).thenReturn(nextBoolean)
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    rng
  }

  def configure(): Unit = bind(classOf[RandomNumberGenerator]).toInstance(stub)
}