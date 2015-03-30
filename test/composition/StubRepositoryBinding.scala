package composition

import com.google.inject.{AbstractModule, TypeLiteral}
import composition.StubFactoryLookupAnyBinding.doesNotTerminateId
import memoization.Memoize2WithSet
import models.common.IScope
import org.mockito.Matchers
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import utils.PozInt

final class StubRepositoryBinding extends AbstractModule with MockitoSugar {

  val stub = {
    val repository: Memoize2WithSet[IScope, PozInt] = mock[Memoize2WithSet[IScope, PozInt]]
    when(repository.size).thenReturn(4)
    when(repository.contains(any[IScope], any[PozInt])).thenReturn(true) // Stub that value is always found.
    when(repository.contains(any[IScope], Matchers.eq(doesNotTerminateId))).thenReturn(false) // Stub that value is never found.
    repository
  }

  override def configure(): Unit = bind(new TypeLiteral[Memoize2WithSet[IScope, PozInt]]() {}).toInstance(stub)
}