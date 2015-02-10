package composition

import com.google.inject.AbstractModule
import decision.AccumulateInstructions
import decision.CreateSeqNodes
import decision.Decision
import models.common.IScope
import models.domain.Step
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar

import scala.concurrent.Future

final class StubCreateSeqNodesBinding extends AbstractModule with MockitoSugar {

  val stub = {
    val createSeqNodes: CreateSeqNodes = mock[CreateSeqNodes]
    val instruction = mock[Step]
    val scope = mock[IScope]
    val accumulateInstructions = AccumulateInstructions(Seq(instruction), scope)
    when(createSeqNodes.create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])).thenReturn(Future.successful(accumulateInstructions))
    createSeqNodes
  }

  override def configure(): Unit = bind(classOf[CreateSeqNodes]).toInstance(stub)
}