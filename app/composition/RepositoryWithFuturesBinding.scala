package composition

import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import memoization.Memoize2
import memoization.RepositoryReturningFutureBool
import models.common.IScope
import utils.PozInt

import scala.concurrent.Future

final class RepositoryWithFuturesBinding extends AbstractModule {

  override def configure(): Unit = bind(new TypeLiteral[Memoize2[IScope, PozInt, Future[Boolean]]]() {}).to(classOf[RepositoryReturningFutureBool]).asEagerSingleton()
}