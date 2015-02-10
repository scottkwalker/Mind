package composition

import com.google.inject.AbstractModule
import decision.CreateNode
import decision.Decision
import models.common.IScope
import models.domain.Step
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar

import scala.concurrent.Future

final class StubCreateNodeBinding extends AbstractModule with MockitoSugar {

  val stub = {
    val createNode = mock[CreateNode]
    val scope = mock[IScope]
    val instruction = mock[Step]
    when(createNode.create(any[Future[Set[Decision]]], any[IScope])).thenReturn(Future.successful(scope, instruction))
    createNode
  }

  override def configure(): Unit = bind(classOf[CreateNode]).toInstance(stub)
}