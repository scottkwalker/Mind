package composition

import com.google.inject.{AbstractModule, TypeLiteral}
import composition.StubFactoryLookupBinding.numberOfFactories
import memoization.Memoize2WithSet
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import utils.PozInt

final class StubRepositoryBinding extends AbstractModule with MockitoSugar {

  val stub = {
    val repository: Memoize2WithSet[IScope, PozInt] = mock[Memoize2WithSet[IScope, PozInt]]
    when(repository.size).thenReturn(numberOfFactories)
    when(repository.apply(any[IScope], any[PozInt])).thenReturn(true) // Stub that value is always found.
    repository
  }

  def configure(): Unit = bind(new TypeLiteral[Memoize2WithSet[IScope, PozInt]]() {}).toInstance(stub)
}