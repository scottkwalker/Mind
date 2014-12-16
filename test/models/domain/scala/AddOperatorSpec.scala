package models.domain.scala

import com.google.inject.Injector
import composition.{StubReplaceEmpty, TestComposition}
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class AddOperatorSpec extends TestComposition {

  "toRawScala" must {
    "return expected" in {
      val left = mock[Instruction]
      when(left.toRaw).thenReturn("STUB_A")
      val right = mock[Instruction]
      when(right.toRaw).thenReturn("STUB_B")

      AddOperator(left=left, right=right).toRaw must equal("STUB_A + STUB_B")
    }
  }

  "hasNoEmpty" must {
    "true given child nodes can terminate in under N steps" in {
      val scope = Scope(height = 2)
      val terminal = ValueRef("v")

      AddOperator(terminal, terminal).hasNoEmpty(scope) must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val scope = Scope(height = 0)
      val nonTerminal = mock[Instruction]
      when(nonTerminal.hasNoEmpty(any[Scope])).thenThrow(new RuntimeException)

      AddOperator(nonTerminal, nonTerminal).hasNoEmpty(scope) must equal(false)
    }

    "false given child nodes cannot terminate in under N steps" in {
      val scope = Scope(height = 10)
      val nonTerminal = mock[Instruction]
      when(nonTerminal.hasNoEmpty(any[Scope])).thenReturn(false)

      AddOperator(nonTerminal, nonTerminal).hasNoEmpty(scope) must equal(false)
    }

    "true when no nodes are empty" in {
      val scope = Scope(height = 10)
      val nonEmpty = ValueRef("v")

      AddOperator(nonEmpty, nonEmpty).hasNoEmpty(scope) must equal(true)
    }

    "false when left node is empty" in {
      val scope = Scope(height = 10)
      val nonEmpty = ValueRef("stub")

      AddOperator(Empty(), nonEmpty).hasNoEmpty(scope) must equal(false)
    }

    "false when right node is empty" in {
      val scope = Scope(height = 10)
      val nonEmpty = ValueRef("stub")

      AddOperator(nonEmpty, Empty()).hasNoEmpty(scope) must equal(false)
    }

    "false given contains a node that is not valid for this level" in {
      val scope = Scope(height = 10)
      val valid = mock[Instruction]
      when(valid.hasNoEmpty(any[Scope])).thenReturn(true)
      val invalid = Object(Seq.empty, "ObjectM0")

      AddOperator(valid, invalid).hasNoEmpty(scope) must equal(false)
    }
  }

  "replaceEmpty" must {
    "calls replaceEmpty on non-empty child nodes" in {
      val scope = mock[IScope]
      implicit val i = mock[Injector]
      val nonEmpty = mock[Instruction]
      when(nonEmpty.replaceEmpty(any[Scope])(any[Injector])).thenReturn(Future.successful(nonEmpty))
      val instance = AddOperator(nonEmpty, nonEmpty)

      val result = instance.replaceEmpty(scope)(i)

      whenReady(result) { _ => verify(nonEmpty, times(2)).replaceEmpty(any[Scope])(any[Injector])}
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val nonEmpty = mock[Instruction]
      when(nonEmpty.replaceEmpty(any[Scope])(any[Injector])).thenReturn(Future.successful(nonEmpty))
      val instance = AddOperator(nonEmpty, nonEmpty)

      val result = instance.replaceEmpty(scope)(injector)

      whenReady(result) {
        _ must equal(instance)
      }
    }

    "returns without empty nodes given there were empty nodes" in {
      val scope = mock[IScope]
      when(scope.numVals).thenReturn(1)
      val empty: Instruction = Empty()
      val injector = testInjector(new StubReplaceEmpty)
      val instance = AddOperator(empty, empty)

      val result = instance.replaceEmpty(scope)(injector)

      whenReady(result) {
        case AddOperator(left, right) =>
          left mustBe a[ValueRef]
          right mustBe a[ValueRef]
      }
    }
  }

  "height" must {
    "return 1 + child height" in {
      val node = mock[Instruction]
      when(node.height).thenReturn(1)

      AddOperator(node, node).height must equal(2)
    }
  }
}