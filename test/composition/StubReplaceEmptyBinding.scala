package composition

import com.google.inject.AbstractModule
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.when
import decision.Decision

import scala.concurrent.Future

final class StubReplaceEmptyBinding extends AbstractModule {

  val stub = {
    val instruction: Step = mock(classOf[Step])
    val node = mock(classOf[Decision])
    when(node.createStep(any[Scope])).thenReturn(Future.successful(instruction))
    node
  }

  override def configure(): Unit = bind(classOf[Decision]).toInstance(stub)
}