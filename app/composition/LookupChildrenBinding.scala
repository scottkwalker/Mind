package composition

import com.google.inject.AbstractModule
import memoization.LookupChildren
import memoization.LookupChildrenImpl

final class LookupChildrenBinding extends AbstractModule {

  override def configure(): Unit =
    bind(classOf[LookupChildren])
      .to(classOf[LookupChildrenImpl])
      .asEagerSingleton()
}
