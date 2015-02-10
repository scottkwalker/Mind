package models.domain.scala

import composition.TestComposition
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class AddOperatorSpec extends TestComposition {

  "toCompilable" must {
    "return expected" in {
      val left = mock[Step]
      when(left.toCompilable).thenReturn("STUB_A")
      val right = mock[Step]
      when(right.toCompilable).thenReturn("STUB_B")

      AddOperator(left = left, right = right).toCompilable must equal("STUB_A + STUB_B")
    }
  }

  "hasNoEmptySteps" must {
    "true given child nodes can terminate in under N steps" in {
      val scope = Scope(height = 2, maxHeight = 10)
      val terminal = ValueRef("v")

      AddOperator(terminal, terminal).hasNoEmptySteps(scope) must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val scope = Scope(height = 0)
      val nonTerminal = mock[Step]
      when(nonTerminal.hasNoEmptySteps(any[Scope])).thenThrow(new RuntimeException)

      AddOperator(nonTerminal, nonTerminal).hasNoEmptySteps(scope) must equal(false)
    }

    "false given child nodes cannot terminate in under N steps" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val nonTerminal = mock[Step]
      when(nonTerminal.hasNoEmptySteps(any[Scope])).thenReturn(false)

      AddOperator(nonTerminal, nonTerminal).hasNoEmptySteps(scope) must equal(false)
    }

    "true when no nodes are empty" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val nonEmpty = ValueRef("v")

      AddOperator(nonEmpty, nonEmpty).hasNoEmptySteps(scope) must equal(true)
    }

    "false when left node is empty" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val nonEmpty = ValueRef("stub")

      AddOperator(Empty(), nonEmpty).hasNoEmptySteps(scope) must equal(false)
    }

    "false when right node is empty" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val nonEmpty = ValueRef("stub")

      AddOperator(nonEmpty, Empty()).hasNoEmptySteps(scope) must equal(false)
    }

    "false given contains a node that is not valid for this level" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val valid = mock[Step]
      when(valid.hasNoEmptySteps(any[Scope])).thenReturn(true)
      val invalid = Object(Seq.empty, "ObjectM0")

      AddOperator(valid, invalid).hasNoEmptySteps(scope) must equal(false)
    }
  }

  "fillEmptySteps" must {
    "calls fillEmptySteps on non-empty child nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val nonEmpty = mock[Step]
      when(nonEmpty.fillEmptySteps(any[Scope], any[FactoryLookup])).thenReturn(Future.successful(nonEmpty))
      val instance = AddOperator(nonEmpty, nonEmpty)

      val result = instance.fillEmptySteps(scope, factoryLookup)

      whenReady(result) { _ => verify(nonEmpty, times(2)).fillEmptySteps(any[Scope], any[FactoryLookup])}(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val nonEmpty = mock[Step]
      when(nonEmpty.fillEmptySteps(any[Scope], any[FactoryLookup])).thenReturn(Future.successful(nonEmpty))
      val instance = AddOperator(nonEmpty, nonEmpty)

      val result = instance.fillEmptySteps(scope, factoryLookup)

      whenReady(result) {
        _ must equal(instance)
      }(config = patienceConfig)
    }

    "returns without empty nodes given there were empty nodes" in {
      val scope = mock[IScope]
      when(scope.numVals).thenReturn(1)
      val empty: Step = Empty()
      val factoryLookup = testInjector().getInstance(classOf[FactoryLookup])
      val instance = AddOperator(empty, empty)

      val result = instance.fillEmptySteps(scope, factoryLookup)

      whenReady(result) {
        case AddOperator(left, right) =>
          left mustBe a[Step]
          right mustBe a[Step]
      }(config = patienceConfig)
    }
  }

  "height" must {
    "return 1 + child height" in {
      val node = mock[Step]
      when(node.height).thenReturn(1)

      AddOperator(node, node).height must equal(2)
    }
  }
}