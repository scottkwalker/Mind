package composition

import com.google.inject.AbstractModule
import memoization.{Generator, LookupChildren}
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import org.mockito.internal.stubbing.answers.DoesNothing
import utils.PozInt

import scala.concurrent.Future

final class StubGenerator(generator: Generator = mock(classOf[Generator])) extends AbstractModule {

  def configure(): Unit = {
    when(generator.generate(any[IScope])).thenReturn(Future.successful(true))
    bind(classOf[Generator]).toInstance(generator)
  }
}