package composition

import com.google.inject.AbstractModule
import replaceEmpty.{CreateNode, CreateNodeImpl}

final class CreateNodeBinding extends AbstractModule {

  override def configure(): Unit = bind(classOf[CreateNode]).to(classOf[CreateNodeImpl]).asEagerSingleton()
}