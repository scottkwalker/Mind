package composition

import com.google.inject.AbstractModule
import replaceEmpty.ReplaceEmpty
import models.common.Scope
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import scala.concurrent.Future

final class StubReplaceEmptyBinding extends AbstractModule {

  def configure(): Unit = {
    val instruction: Instruction = mock(classOf[Instruction])
    val node = mock(classOf[ReplaceEmpty])
    when(node.create(any[Scope])).thenReturn(Future.successful(instruction))
    bind(classOf[ReplaceEmpty]).toInstance(node)
  }
}