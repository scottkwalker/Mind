package composition

import com.google.inject.AbstractModule
import replaceEmpty.ReplaceEmpty
import models.common.Scope
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}

final class StubReplaceEmpty extends AbstractModule {

  def configure(): Unit = {
    val n: Instruction = mock(classOf[Instruction])
    val f = mock(classOf[ReplaceEmpty])
    when(f.create(any[Scope])).thenReturn(n)
    bind(classOf[ReplaceEmpty]).toInstance(f)
  }
}