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
      when(ai.chooseChild(any[Seq[ReplaceEmpty]], any[Scope])).thenReturn(v)
      val sut = CreateNodeImpl(ai)

      val (_, _) = sut.create(Future.successful(Seq(v)), scope)

      verify(ai, times(1)).chooseChild(Seq(v), scope)
    }

    "calls updateScope" in {
      val scope = Scope(height = 10)
      val v = mock[ReplaceEmpty]
      when(v.updateScope(scope)).thenReturn(scope)
      val ai = mock[SelectionStrategy]
      when(ai.chooseChild(any[Seq[ReplaceEmpty]], any[Scope])).thenReturn(v)
      val sut = CreateNodeImpl(ai)

      val (_, _) = sut.create(Future.successful(Seq(v)), scope)

      verify(v, times(1)).updateScope(scope)
    }

    "calls create on factory" in {
      val scope = Scope(height = 10)
      val v = mock[ReplaceEmpty]
      when(v.updateScope(scope)).thenReturn(scope)
      val ai = mock[SelectionStrategy]
      when(ai.chooseChild(any[Seq[ReplaceEmpty]], any[Scope])).thenReturn(v)
      val sut = CreateNodeImpl(ai)

      val (_, _) = sut.create(Future.successful(Seq(v)), scope)

      verify(v, times(1)).create(scope)
    }
  }
}