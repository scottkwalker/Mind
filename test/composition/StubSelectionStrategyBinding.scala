package composition

import _root_.ai.SelectionStrategy
import com.google.inject.AbstractModule
import decision.Decision
import models.common.IScope
import models.domain.Step
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar

import scala.concurrent.Future

final class StubSelectionStrategyBinding(chooseIndex: Int = 0) extends AbstractModule with MockitoSugar {

  val stubDecision = {
    val scope = mock[IScope]
    val instruction = mock[Step]
    val decision = mock[Decision]
    when(decision.fillEmptySteps(any[IScope])).thenReturn(Future.successful(instruction))
    when(decision.updateScope(any[IScope])).thenReturn(scope)
    decision
  }

  val stub = {
    val selectionStrategy = mock[SelectionStrategy]
    when(selectionStrategy.chooseChild(any[Set[Decision]])).thenReturn(stubDecision)
    when(selectionStrategy.chooseIndex(any[Int])).thenReturn(chooseIndex)
    when(selectionStrategy.chooseChild(any[Future[Set[Decision]]])).thenReturn(Future.successful(stubDecision))
    when(selectionStrategy.canAddAnother(any[Int], any[Int])).thenReturn(false)
    when(selectionStrategy.generateLengthOfSeq(any[Int])).thenReturn(1)
    selectionStrategy
  }

  override def configure(): Unit = bind(classOf[SelectionStrategy]).toInstance(stub)
}