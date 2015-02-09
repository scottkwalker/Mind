package composition

import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import memoization.Memoize2
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import utils.PozInt

import scala.concurrent.Future

final class StubRepositoryWithFuture(size: Int = 0) extends AbstractModule with MockitoSugar {

  val stub = {
    val repositoryWithFutures = mock[Memoize2[IScope, PozInt, Future[Boolean]]]
    when(repositoryWithFutures.size).thenReturn(size)
    when(repositoryWithFutures.apply(any[IScope], any[PozInt])).thenReturn(Future.successful(true))
    repositoryWithFutures
  }

  override def configure(): Unit = bind(new TypeLiteral[Memoize2[IScope, PozInt, Future[Boolean]]]() {}).toInstance(stub)
}