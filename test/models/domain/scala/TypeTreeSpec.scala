package models.domain.scala

import composition.StubFactoryLookupAnyBinding
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.ScopeHelper._

import scala.concurrent.Future

final class TypeTreeSpec extends UnitTestHelpers with TestComposition {

  "hasNoEmptySteps" must {
    "return true if it can terminates in under N steps" in {
      val step = mock[Object]
      when(step.hasNoEmptySteps(any[IScope])).thenReturn(true)
      val typeTree = build(step)

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(true)
    }

    "return false if it cannot terminate in under N steps" in {
      val step = mock[Step]
      when(step.hasNoEmptySteps(any[IScope])).thenReturn(false)
      val typeTree = build(step)

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scopeWithoutHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "return false if the root node is empty" in {
      val typeTree = build(Empty())

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "return false if scope has no height remaining" in {
      val typeTree = build()

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scopeWithoutHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "throw an exception if there is a node of an unhandled node type" in {
      val unhandledNode = mock[Step]
      val typeTree = build(unhandledNode)

      a[RuntimeException] must be thrownBy typeTree.hasNoEmptySteps(scopeWithHeightRemaining)
    }
  }

  "fillEmptySteps" must {
    "calls fillEmptySteps and updateScope once on non-empty child nodes" in {
      val factoryLookup = mock[FactoryLookup]
      val step = mock[Step]
      when(step.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(step)
      val typeTree = build(step)

      val fillEmptySteps = typeTree.fillEmptySteps(scope(), factoryLookup)

      whenReady(fillEmptySteps) { _ =>
        verify(step, times(1)).fillEmptySteps(any[IScope], any[FactoryLookup])
        verify(step, times(1)).updateScope(any[IScope])
        verifyNoMoreInteractions(step)
      }(config = patienceConfig)
    }

    "return the same if there are no empty nodes" in {
      val factoryLookup = mock[FactoryLookup]
      val step = mock[Step]
      when(step.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(step)
      val typeTree = build(step)

      val fillEmptySteps = typeTree.fillEmptySteps(scope(), factoryLookup)

      whenReady(fillEmptySteps) {
        _ must equal(typeTree)
      }(config = patienceConfig)
    }

    "return without empty nodes if there were empty nodes" in {
      val scope = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 10,
        maxObjectsInTree = 1,
        maxHeight = 10)
      val empty = Empty()
      val factoryLookup = testInjector(
        new StubFactoryLookupAnyBinding,
        new StubSelectionStrategyBinding
      ).getInstance(classOf[FactoryLookup])
      val typeTree = build(empty)

      val fillEmptySteps = typeTree.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) {
        case TypeTree(nodes) =>
          nodes match {
            case Seq(nonEmpty) => nonEmpty mustBe an[Step]
            case _ => fail("not a seq")
          }
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "throw an exception if nodes list is empty (no empty or non-empty)" in {
      val factoryLookup = mock[FactoryLookup]
      val typeTree = new TypeTree(nodes = Seq.empty)

      a[RuntimeException] must be thrownBy typeTree.fillEmptySteps(scope(), factoryLookup).futureValue
    }
  }

  "height" must {
    "return 1 + child height when has one child" in {
      val step = mock[Step]
      when(step.height).thenReturn(2)
      val typeTree = build(step)

      val height = typeTree.height

      height must equal(3)
    }

    "return 1 + child height when has two children" in {
      val step1 = mock[Step]
      when(step1.height).thenReturn(1)
      val step2 = mock[Step]
      when(step2.height).thenReturn(2)
      val typeTree = build(Seq(step1, step2))

      val height = typeTree.height

      height must equal(3)
    }

    "return expected value for realistic tree" in {
      val typeTree = build(
        Seq(
          ObjectImpl(Seq(
            FunctionMImpl(params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM())),
              nodes = Seq(
                AddOperatorImpl(
                  ValueRefImpl("a"), ValueRefImpl("b"))
              ), name = "f0")),
            name = "o0")))

      val height = typeTree.height

      height must equal(5)
    }
  }

  private def build(step: Step = mock[Step]) = TypeTree(Seq(step))

  private def build(step: Seq[Step]) = TypeTree(step)
}