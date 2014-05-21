package nodes.helpers

import ai.SelectionStrategy
import utils.helpers.UnitSpec
import org.mockito.Mockito._
import org.mockito.Matchers.any

final class CreateNodeSpec extends UnitSpec {
  "create" should {
    "calls chooseChild on ai" in {
      val scope = Scope(maxDepth = 10)
      val v = mock[ICreateChildNodes]
      when(v.updateScope(scope)).thenReturn(scope)
      val ai = mock[SelectionStrategy]
      when(ai.chooseChild(any[Seq[ICreateChildNodes]], any[Scope])).thenReturn(v)
      val sut = CreateNode()

      val (_, _) = sut.create(Seq(v), scope, ai)

      verify(ai, times(1)).chooseChild(Seq(v), scope)
    }

    "calls updateScope" in {
      val scope = Scope(maxDepth = 10)
      val v = mock[ICreateChildNodes]
      when(v.updateScope(scope)).thenReturn(scope)
      val ai = mock[SelectionStrategy]
      when(ai.chooseChild(any[Seq[ICreateChildNodes]], any[Scope])).thenReturn(v)
      val sut = CreateNode()

      val (_, _) = sut.create(Seq(v), scope, ai)

      verify(v, times(1)).updateScope(scope)
    }

    "calls create on factory" in {
      val scope = Scope(maxDepth = 10)
      val v = mock[ICreateChildNodes]
      when(v.updateScope(scope)).thenReturn(scope)
      val ai = mock[SelectionStrategy]
      when(ai.chooseChild(any[Seq[ICreateChildNodes]], any[Scope])).thenReturn(v)
      val sut = CreateNode()

      val (_, _) = sut.create(Seq(v), scope, ai)

      verify(v, times(1)).create(scope)
    }
  }
}