package models.domain.scala

import composition.StubFactoryLookupBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.ScopeHelper._

import scala.concurrent.Future

final class AddOperatorSpec extends UnitTestHelpers with TestComposition {

  "toCompilable" must {
    "return expected" in {
      val left = mock[Step]
      when(left.toCompilable).thenReturn("STUB_A")
      val right = mock[Step]
      when(right.toCompilable).thenReturn("STUB_B")

      val compilable = AddOperatorImpl(left = left, right = right).toCompilable

      compilable must equal("STUB_A + STUB_B")
    }
  }

  "hasNoEmptySteps" must {
    "true given child nodes can terminate in under N steps" in {
      val terminal = mock[ValueRef]
      when(terminal.hasNoEmptySteps(any[IScope])).thenReturn(true)

      val hasNoEmptySteps = AddOperatorImpl(terminal, terminal).hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val nonTerminal = mock[Step]
      when(nonTerminal.hasNoEmptySteps(any[IScope])).thenThrow(new RuntimeException)

      val hasNoEmptySteps = AddOperatorImpl(nonTerminal, nonTerminal).hasNoEmptySteps(scopeWithoutHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "false given child nodes cannot terminate in under N steps" in {
      val nonTerminal = mock[Step]
      when(nonTerminal.hasNoEmptySteps(any[IScope])).thenReturn(false)

      val hasNoEmptySteps = AddOperatorImpl(nonTerminal, nonTerminal).hasNoEmptySteps(scopeWithoutHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "false when left node is empty" in {
      val nonEmpty = ValueRefImpl("stub")

      val hasNoEmptySteps = AddOperatorImpl(Empty(), nonEmpty).hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "false when right node is empty" in {
      val nonEmpty = ValueRefImpl("stub")

      val hasNoEmptySteps = AddOperatorImpl(nonEmpty, Empty()).hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "throw when there is a node of an unhandled node type" in {
      val unhandledNode = mock[Step]
      val objectImpl = new AddOperatorImpl(left = unhandledNode, right = unhandledNode)

      a[RuntimeException] must be thrownBy objectImpl.hasNoEmptySteps(scopeWithHeightRemaining)
    }
  }

  "fillEmptySteps" must {
    "calls fillEmptySteps on non-empty child nodes" in {
      val factoryLookup = mock[FactoryLookup]
      val nonEmpty = mock[Step]
      when(nonEmpty.fillEmptySteps(any[IScope], any[FactoryLookup])).thenReturn(Future.successful(nonEmpty))
      val instance = AddOperatorImpl(nonEmpty, nonEmpty)

      val fillEmptySteps = instance.fillEmptySteps(scopeWithHeightRemaining, factoryLookup)

      whenReady(fillEmptySteps) { _ =>
        verify(nonEmpty, times(2)).fillEmptySteps(any[IScope], any[FactoryLookup])
      }(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val factoryLookup = mock[FactoryLookup]
      val nonEmpty = mock[Step]
      when(nonEmpty.fillEmptySteps(any[IScope], any[FactoryLookup])).thenReturn(Future.successful(nonEmpty))
      val instance = AddOperatorImpl(nonEmpty, nonEmpty)

      val fillEmptySteps = instance.fillEmptySteps(scopeWithHeightRemaining, factoryLookup)

      whenReady(fillEmptySteps) {
        _ must equal(instance)
      }(config = patienceConfig)
    }

    "returns without empty nodes given there were empty nodes" in {
      val empty: Step = Empty()
      val factoryLookup = testInjector(new StubFactoryLookupBinding).getInstance(classOf[FactoryLookup])
      val instance = AddOperatorImpl(empty, empty)

      val fillEmptySteps = instance.fillEmptySteps(scopeWithNumVals(1), factoryLookup)

      whenReady(fillEmptySteps) {
        case AddOperatorImpl(left, right) =>
          left mustBe a[Step]
          right mustBe a[Step]
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }
  }

  "height" must {
    "return 1 + child height" in {
      val height = AddOperatorImpl(stepWithHeight(1), stepWithHeight(1)).height
      height must equal(2)
    }
  }

  def stepWithHeight(height: Int) = {
    val step = mock[Step]
    when(step.height).thenReturn(height)
    step
  }
}