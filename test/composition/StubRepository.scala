package composition

import com.google.inject.{AbstractModule, TypeLiteral}
import composition.StubFactoryLookup.numberOfFactories
import memoization.Memoize2WithSet
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import utils.PozInt

final class StubRepository(repository: Memoize2WithSet[IScope, PozInt] = mock(classOf[Memoize2WithSet[IScope, PozInt]])) extends AbstractModule with MockitoSugar {

  def configure(): Unit = {
    when(repository.size).thenReturn(numberOfFactories)
    when(repository.apply(any[IScope], any[PozInt])).thenReturn(true) // Stub that value is always found.
    bind(new TypeLiteral[Memoize2WithSet[IScope, PozInt]]() {}).toInstance(repository)
  }
}