package composition

import com.google.inject.{AbstractModule, TypeLiteral}
import memoization.{Memoize2WithSet, RepositoryWithSetImpl}
import models.common.IScope
import utils.PozInt

final class RepositoryBinding extends AbstractModule {

  override def configure(): Unit = bind(new TypeLiteral[Memoize2WithSet[IScope, PozInt]]() {}).to(classOf[RepositoryWithSetImpl]).asEagerSingleton()
}