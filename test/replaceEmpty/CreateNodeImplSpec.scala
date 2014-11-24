package replaceEmpty

import ai.SelectionStrategy
import composition.TestComposition
import models.common.Scope
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito._

import scala.concurrent.Future

final class CreateNodeImplSpec extends TestComposition {

  "create" must {
    "calls chooseChild on ai" in {
      val (_, scope, ai, possibleChildren) = build

      val sut = CreateNodeImpl(ai)

      whenReady(sut.create(possibleChildren, scope), browserTimeout) { _ =>
        verify(ai, times(1)).chooseChild(possibleChildren, scope)
      }
    }

    "calls updateScope" in {
      val (replaceEmpty, scope, ai, possibleChildren) = build

      val sut = CreateNodeImpl(ai)

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(replaceEmpty, times(1)).updateScope(scope)
      }
    }

    "calls create on factory" in {
      val (replaceEmpty, scope, ai, possibleChildren) = build

      val sut = CreateNodeImpl(ai)

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(replaceEmpty, times(1)).create(scope)
      }
    }
  }

  private def build = {
    val scope = Scope(height = 10)
    val instruction = mock[Instruction]
    val replaceEmpty = mock[ReplaceEmpty]
    when(replaceEmpty.create(scope)).thenReturn(Future.successful(instruction))
    when(replaceEmpty.updateScope(scope)).thenReturn(scope)
    val ai = mock[SelectionStrategy]
    when(ai.chooseChild(any[Future[Seq[ReplaceEmpty]]], any[Scope])).thenReturn(Future.successful(replaceEmpty))
    val possibleChildren = Future.successful(Seq(replaceEmpty))
    (replaceEmpty, scope, ai, possibleChildren)
  }
}