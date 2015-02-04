package composition

import com.google.inject.{AbstractModule, TypeLiteral}
import memoization.{Memoize2, RepositoryReturningBool}
import models.common.IScope
import utils.PozInt

final class RepositoryReturningBoolBinding extends AbstractModule {

  override def configure(): Unit = bind(new TypeLiteral[Memoize2[IScope, PozInt, Boolean]]() {}).to(classOf[RepositoryReturningBool]).asEagerSingleton()
}