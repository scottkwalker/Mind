package composition

import com.google.inject.AbstractModule
import decision.CreateNode
import decision.CreateNodeImpl

final class CreateNodeBinding extends AbstractModule {

  override def configure(): Unit = bind(classOf[CreateNode]).to(classOf[CreateNodeImpl]).asEagerSingleton()
}