package composition

import _root_.ai.SelectionStrategy
import com.google.inject.AbstractModule
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import replaceEmpty.ReplaceEmpty

import scala.concurrent.Future

final class StubSelectionStrategy extends AbstractModule with MockitoSugar {

  def configure(): Unit = {
    val scope = Scope(height = 10, maxHeight = 10)
    val instruction = mock[Instruction]
    val replaceEmpty = mock[ReplaceEmpty]
    when(replaceEmpty.create(any[IScope])).thenReturn(Future.successful(instruction))
    when(replaceEmpty.updateScope(any[IScope])).thenReturn(scope)
    val selectionStrategy = mock[SelectionStrategy]
    when(selectionStrategy.chooseChild(any[Set[ReplaceEmpty]])).thenReturn(replaceEmpty)
    when(selectionStrategy.chooseIndex(any[Int])).thenReturn(0)
    when(selectionStrategy.chooseChild(any[Future[Set[ReplaceEmpty]]])).thenReturn(Future.successful(replaceEmpty))
    when(selectionStrategy.canAddAnother(any[Int], any[Int])).thenReturn(false)
    when(selectionStrategy.generateLengthOfSeq(any[Int])).thenReturn(1)
    bind(classOf[SelectionStrategy]).toInstance(selectionStrategy)
  }
}