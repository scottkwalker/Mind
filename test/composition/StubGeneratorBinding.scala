package composition

import com.google.inject.AbstractModule
import memoization.Generator
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar

import scala.concurrent.Future

final class StubGeneratorBinding extends AbstractModule with MockitoSugar {

  val stub = {
    val generator: Generator = mock[Generator]
    when(generator.calculateAndUpdate(any[IScope])).thenReturn(Future.successful(1))
    generator
  }

  def configure(): Unit = bind(classOf[Generator]).toInstance(stub)
}