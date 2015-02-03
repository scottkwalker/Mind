package composition

import com.google.inject.AbstractModule
import models.common.Scope
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import replaceEmpty.ReplaceEmpty

import scala.concurrent.Future

final class StubReplaceEmptyBinding extends AbstractModule {

  val stub = {
    val instruction: Instruction = mock(classOf[Instruction])
    val node = mock(classOf[ReplaceEmpty])
    when(node.create(any[Scope])).thenReturn(Future.successful(instruction))
    node
  }

  def configure(): Unit = bind(classOf[ReplaceEmpty]).toInstance(stub)
}