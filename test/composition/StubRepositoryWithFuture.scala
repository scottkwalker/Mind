package composition

import com.google.inject.{AbstractModule, TypeLiteral}
import memoization.Memoize2
import models.common.IScope
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import utils.PozInt

import scala.concurrent.Future

final class StubRepositoryWithFuture extends AbstractModule with MockitoSugar {

  val stub = {
    val repositoryWithFutures = mock[Memoize2[IScope, PozInt, Future[Boolean]]]
    when(repositoryWithFutures.size).thenReturn(0)
    repositoryWithFutures
  }

  override def configure(): Unit = bind(new TypeLiteral[Memoize2[IScope, PozInt, Future[Boolean]]]() {}).toInstance(stub)
}