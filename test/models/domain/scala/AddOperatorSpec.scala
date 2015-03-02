package models.domain.scala

import composition.StubFactoryLookupBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class AddOperatorSpec extends UnitTestHelpers with TestComposition {

  "toCompilable" must {
    "return expected" in {
      val left = mock[Step]
      when(left.toCompilable).thenReturn("STUB_A")
      val right = mock[Step]
      when(right.toCompilable).thenReturn("STUB_B")

      val compilable = AddOperator(left = left, right = right).toCompilable

      compilable must equal("STUB_A + STUB_B")
    }
  }

  "hasNoEmptySteps" must {
    "true given child nodes can terminate in under N steps" in {
      val scopeDecremented = mock[IScope]
      when(scopeDecremented.height).thenReturn(1)
      when(scopeDecremented.maxHeight).thenReturn(10)
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      when(scope.height).thenReturn(2)
      when(scope.decrementHeight).thenReturn(scopeDecremented)
      val terminal = ValueRef("v")

      val hasNoEmptySteps = AddOperator(terminal, terminal).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val scope = mock[IScope]
      val nonTerminal = mock[Step]
      when(nonTerminal.hasNoEmptySteps(any[Scope])).thenThrow(new RuntimeException)

      val hasNoEmptySteps = AddOperator(nonTerminal, nonTerminal).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false given child nodes cannot terminate in under N steps" in {
      val scope = mock[IScope]
      val nonTerminal = mock[Step]
      when(nonTerminal.hasNoEmptySteps(any[Scope])).thenReturn(false)

      val hasNoEmptySteps = AddOperator(nonTerminal, nonTerminal).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "true when no nodes are empty" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val nonEmpty = ValueRef("v")

      val hasNoEmptySteps = AddOperator(nonEmpty, nonEmpty).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "false when left node is empty" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val nonEmpty = ValueRef("stub")

      val hasNoEmptySteps = AddOperator(Empty(), nonEmpty).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false when right node is empty" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val nonEmpty = ValueRef("stub")

      val hasNoEmptySteps = AddOperator(nonEmpty, Empty()).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false given contains a node that is not valid for this level" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val valid = mock[Step]
      when(valid.hasNoEmptySteps(any[Scope])).thenReturn(true)
      val invalid = Object(Seq.empty, "ObjectM0")

      val hasNoEmptySteps = AddOperator(valid, invalid).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }
  }

  "fillEmptySteps" must {
    "calls fillEmptySteps on non-empty child nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val nonEmpty = mock[Step]
      when(nonEmpty.fillEmptySteps(any[Scope], any[FactoryLookup])).thenReturn(Future.successful(nonEmpty))
      val instance = AddOperator(nonEmpty, nonEmpty)

      val fillEmptySteps = instance.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) { _ =>
        verify(nonEmpty, times(2)).fillEmptySteps(any[Scope], any[FactoryLookup])
      }(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val nonEmpty = mock[Step]
      when(nonEmpty.fillEmptySteps(any[Scope], any[FactoryLookup])).thenReturn(Future.successful(nonEmpty))
      val instance = AddOperator(nonEmpty, nonEmpty)

      val fillEmptySteps = instance.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) {
        _ must equal(instance)
      }(config = patienceConfig)
    }

    "returns without empty nodes given there were empty nodes" in {
      val scope = mock[IScope]
      when(scope.numVals).thenReturn(1)
      val empty: Step = Empty()
      val factoryLookup = testInjector(new StubFactoryLookupBinding).getInstance(classOf[FactoryLookup])
      val instance = AddOperator(empty, empty)

      val fillEmptySteps = instance.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) {
        case AddOperator(left, right) =>
          left mustBe a[Step]
          right mustBe a[Step]
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }
  }

  "height" must {
    "return 1 + child height" in {
      val node = mock[Step]
      when(node.height).thenReturn(1)

      val height = AddOperator(node, node).height

      height must equal(2)
    }
  }
}