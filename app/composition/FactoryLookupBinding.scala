package composition

import com.google.inject.AbstractModule
import models.domain.scala.FactoryLookup
import models.domain.scala.FactoryLookupImpl

final class FactoryLookupBinding extends AbstractModule {

  override def configure(): Unit =
    bind(classOf[FactoryLookup]).to(classOf[FactoryLookupImpl])
}
