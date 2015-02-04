package composition

import com.google.inject.AbstractModule
import memoization.{LookupChildren, LookupChildrenImpl}

final class LookupChildrenBinding extends AbstractModule {

  override def configure(): Unit = bind(classOf[LookupChildren]).to(classOf[LookupChildrenImpl]).asEagerSingleton()
}