package composition

import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import memoization.Memoize2
import memoization.RepositoryReturningBool
import models.common.IScope
import utils.PozInt

final class RepositoryReturningBoolBinding extends AbstractModule {

  override def configure(): Unit =
    bind(new TypeLiteral[Memoize2[IScope, PozInt, Boolean]]() {})
      .to(classOf[RepositoryReturningBool])
      .asEagerSingleton()
}
