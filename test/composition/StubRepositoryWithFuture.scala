package composition

import com.google.inject.{AbstractModule, TypeLiteral}
import memoization.Memoize2
import models.common.IScope
import org.mockito.Mockito._
import utils.PozInt

import scala.concurrent.Future

final class StubRepositoryWithFuture(repositoryWithFutures: Memoize2[IScope, PozInt, Future[Boolean]]) extends AbstractModule {

  def configure(): Unit = {
    when(repositoryWithFutures.size).thenReturn(0)
    bind(new TypeLiteral[Memoize2[IScope, PozInt, Future[Boolean]]]() {}).toInstance(repositoryWithFutures)
  }
}