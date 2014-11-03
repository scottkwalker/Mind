package replaceEmpty

import ai.SelectionStrategy
import composition.TestComposition
import models.common.Scope
import org.mockito.Matchers.any
import org.mockito.Mockito._
import scala.concurrent.Future

final class CreateNodeImplSpec extends TestComposition {

  "create" must {
    "calls chooseChild on ai" in {
      val scope = Scope(height = 10)
      val v = mock[ReplaceEmpty]
      when(v.updateScope(scope)).thenReturn(scope)
      val ai = mock[SelectionStrategy]
      when(ai.chooseChild(any[Future[Seq[ReplaceEmpty]]], any[Scope])).thenReturn(Future.successful(v))
      val possibleChildren = Future.successful(Seq(v))
      val sut = CreateNodeImpl(ai)

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(ai, times(1)).chooseChild(possibleChildren, scope)
      }
    }

    "calls updateScope" in {
      val scope = Scope(height = 10)
      val v = mock[ReplaceEmpty]
      when(v.updateScope(scope)).thenReturn(scope)
      val ai = mock[SelectionStrategy]
      when(ai.chooseChild(any[Future[Seq[ReplaceEmpty]]], any[Scope])).thenReturn(Future.successful(v))
      val possibleChildren = Future.successful(Seq(v))
      val sut = CreateNodeImpl(ai)

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(v, times(1)).updateScope(scope)
      }
    }

    "calls create on factory" in {
      val scope = Scope(height = 10)
      val v = mock[ReplaceEmpty]
      when(v.updateScope(scope)).thenReturn(scope)
      val ai = mock[SelectionStrategy]
      when(ai.chooseChild(any[Future[Seq[ReplaceEmpty]]], any[Scope])).thenReturn(Future.successful(v))
      val possibleChildren = Future.successful(Seq(v))
      val sut = CreateNodeImpl(ai)

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(v, times(1)).create(scope)
      }
    }
  }
}