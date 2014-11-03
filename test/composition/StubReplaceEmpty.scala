package composition

import com.google.inject.AbstractModule
import replaceEmpty.ReplaceEmpty
import models.common.Scope
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import scala.concurrent.Future

final class StubReplaceEmpty extends AbstractModule {

  def configure(): Unit = {
    val n: Instruction = mock(classOf[Instruction])
    val f = mock(classOf[ReplaceEmpty])
    when(f.create(any[Scope])).thenReturn(Future.successful(n))
    bind(classOf[ReplaceEmpty]).toInstance(f)
  }
}