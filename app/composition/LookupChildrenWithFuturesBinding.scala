package composition

import com.google.inject.AbstractModule
import memoization.{LookupChildrenWithFutures, LookupChildrenWithFuturesImpl}

final class LookupChildrenWithFuturesBinding extends AbstractModule {

  override def configure(): Unit = bind(classOf[LookupChildrenWithFutures]).to(classOf[LookupChildrenWithFuturesImpl]).asEagerSingleton()
}