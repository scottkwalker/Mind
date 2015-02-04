package composition

import com.google.inject.AbstractModule
import replaceEmpty._

final class CreateSeqNodesBinding extends AbstractModule {

  override def configure(): Unit = bind(classOf[CreateSeqNodes]).to(classOf[CreateSeqNodesImpl]).asEagerSingleton()
}