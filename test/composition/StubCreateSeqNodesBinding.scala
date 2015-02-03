package composition

import com.google.inject.AbstractModule
import models.common.IScope
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import replaceEmpty.{AccumulateInstructions, CreateSeqNodes, ReplaceEmpty}

import scala.concurrent.Future

final class StubCreateSeqNodesBinding extends AbstractModule with MockitoSugar {

  val stub = {
    val createSeqNodes: CreateSeqNodes = mock[CreateSeqNodes]
    val instruction = mock[Instruction]
    val scope = mock[IScope]
    val accumulateInstructions = AccumulateInstructions(Seq(instruction), scope)
    when(createSeqNodes.create(any[Future[Set[ReplaceEmpty]]], any[IScope], any[Seq[Instruction]], any[Int])).thenReturn(Future.successful(accumulateInstructions))
    createSeqNodes
  }

  override def configure(): Unit = bind(classOf[CreateSeqNodes]).toInstance(stub)
}