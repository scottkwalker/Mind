package models.domain.scala

import composition.StubFactoryLookupBinding
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
    "return true given it can terminates in under N steps" in {
      val step = mock[Object]
      when(step.hasNoEmptySteps(any[IScope])).thenReturn(true)
      val typeTree = new TypeTree(Seq(step))

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(true)
    }

    "return false given it cannot terminate in under N steps" in {
      val step = mock[Step]
      when(step.hasNoEmptySteps(any[IScope])).thenReturn(false)
      val typeTree = new TypeTree(Seq(step))

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scopeWithoutHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "return false given empty root node" in {
      val typeTree = new TypeTree(Seq(Empty()))

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "return false when scope hasNoEmptySteps returns false" in {
      val step = mock[Step]
      val typeTree = new TypeTree(Seq(step))

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scopeWithoutHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "throw when there is a node of an unhandled node type" in {
      val unhandledNode = mock[Step]
      val typeTree = new TypeTree(Seq(unhandledNode))

      a[RuntimeException] must be thrownBy typeTree.hasNoEmptySteps(scopeWithHeightRemaining)
    }
  }

  "fillEmptySteps" must {
    "calls fillEmptySteps on non-empty child nodes" in {
      val factoryLookup = mock[FactoryLookup]
      val step = mock[Step]
      when(step.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(step)
      val typeTree = TypeTree(Seq(step))

      val fillEmptySteps = typeTree.fillEmptySteps(scope(), factoryLookup)

      whenReady(fillEmptySteps) { _ => verify(step, times(1)).fillEmptySteps(any[IScope], any[FactoryLookup])}(config = patienceConfig)
    }

    "return same when no empty nodes" in {
      val factoryLookup = mock[FactoryLookup]
      val step = mock[Step]
      when(step.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(step)
      val typeTree = new TypeTree(Seq(step))

      val fillEmptySteps = typeTree.fillEmptySteps(scope(), factoryLookup)

      whenReady(fillEmptySteps) {
        _ must equal(typeTree)
      }(config = patienceConfig)
    }

    "return without empty nodes given there were empty nodes" in {
      val scope = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 10,
        maxObjectsInTree = 1,
        maxHeight = 10)
      val empty = Empty()
      val factoryLookup = testInjector(
        new StubFactoryLookupBinding,
        new StubSelectionStrategyBinding
      ).getInstance(classOf[FactoryLookup])
      val typeTree = TypeTree(nodes = Seq(empty))

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

    "throw when passed empty seq (no empty or non-empty)" in {
      val factoryLookup = mock[FactoryLookup]
      val typeTree = new TypeTree(nodes = Seq.empty)

      a[RuntimeException] must be thrownBy typeTree.fillEmptySteps(scope(), factoryLookup).futureValue
    }
  }

  "height" must {
    "return 1 + child height when has one child" in {
      val step = mock[Step]
      when(step.height).thenReturn(2)
      val typeTree = new TypeTree(Seq(step))

      val height = typeTree.height

      height must equal(3)
    }

    "return 1 + child height when has two children" in {
      val step1 = mock[Step]
      when(step1.height).thenReturn(1)
      val step2 = mock[Step]
      when(step2.height).thenReturn(2)
      val typeTree = new TypeTree(Seq(step1, step2))

      val height = typeTree.height

      height must equal(3)
    }

    "return expected value for realistic tree" in {
      val typeTree = new TypeTree(
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
}