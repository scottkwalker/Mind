package composition

import com.google.inject.AbstractModule
import models.common.IScope
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import replaceEmpty.{CreateNode, ReplaceEmpty}

import scala.concurrent.Future

final class StubCreateNodeBinding extends AbstractModule with MockitoSugar {

  val stub = {
    val createNode = mock[CreateNode]
    val scope = mock[IScope]
    val instruction = mock[Instruction]
    when(createNode.create(any[Future[Set[ReplaceEmpty]]], any[IScope])).thenReturn(Future.successful(scope, instruction))
    createNode
  }

  override def configure(): Unit = bind(classOf[CreateNode]).toInstance(stub)
}