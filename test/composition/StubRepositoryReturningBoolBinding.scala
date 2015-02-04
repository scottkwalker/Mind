package composition

import com.google.inject.{AbstractModule, TypeLiteral}
import memoization.Memoize2
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import utils.PozInt

final class StubRepositoryReturningBoolBinding extends AbstractModule with MockitoSugar {

  val stub = {
    val repository: Memoize2[IScope, PozInt, Boolean] = mock[Memoize2[IScope, PozInt, Boolean]]
    when(repository.size).thenReturn(4)
    when(repository.apply(any[IScope], any[PozInt])).thenReturn(true) // Stub that value is always found.
    when(repository.funcCalculate(any[IScope], any[PozInt])).thenReturn(true)
    repository
  }

  override def configure(): Unit = bind(new TypeLiteral[Memoize2[IScope, PozInt, Boolean]]() {}).toInstance(stub)
}