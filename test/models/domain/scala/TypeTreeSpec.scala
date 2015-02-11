package models.domain.scala

import composition.TestComposition
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Await
import scala.concurrent.Future

final class TypeTreeSpec extends TestComposition {

  "hasNoEmptySteps" must {
    "return true given it can terminates in under N steps" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val instruction = Object(nodes = Seq.empty, name = "o0")
      val typeTree = new TypeTree(Seq(instruction))

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "return false given it cannot terminate in under N steps" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val instruction = mock[Step]
      when(instruction.hasNoEmptySteps(any[Scope])).thenReturn(false)
      val typeTree = new TypeTree(Seq(instruction))

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "return true given none empty" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val instruction = Object(nodes = Seq.empty, name = "o0")
      val typeTree = new TypeTree(Seq(instruction))

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "return false given empty root node" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val typeTree = new TypeTree(Seq(Empty()))

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "return false when hasHeightRemaining returns false" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(false)
      val instruction = mock[Step]
      val typeTree = new TypeTree(Seq(instruction))

      val hasNoEmptySteps = typeTree.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }
  }

  "fillEmptySteps" must {
    "calls fillEmptySteps on non-empty child nodes" in {
      val scope = mock[IScope]
      val injector = mock[FactoryLookup]
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val instance = TypeTree(Seq(instruction))

      val step = instance.fillEmptySteps(scope, injector)

      whenReady(step) { _ => verify(instruction, times(1)).fillEmptySteps(any[Scope], any[FactoryLookup])}(config = patienceConfig)
    }

    "return same when no empty nodes" in {
      val scope = mock[IScope]
      val injector = mock[FactoryLookup]
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val instance = new TypeTree(Seq(instruction))

      val step = instance.fillEmptySteps(scope, injector)

      whenReady(step) {
        _ must equal(instance)
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
      val injector = testInjector().getInstance(classOf[FactoryLookup])
      val instance = TypeTree(nodes = Seq(empty))

      val step = instance.fillEmptySteps(scope, injector)

      whenReady(step) {
        case TypeTree(nodes) =>
          nodes match {
            case Seq(nonEmpty) => nonEmpty mustBe an[Step]
            case _ => fail("not a seq")
          }
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "throw when passed empty seq (no empty or non-empty)" in {
      val scope = mock[IScope]
      val injector = mock[FactoryLookup]
      val instance = new TypeTree(nodes = Seq.empty)

      a[RuntimeException] must be thrownBy Await.result(instance.fillEmptySteps(scope, injector), finiteTimeout)
    }
  }

  "height" must {
    "return 1 + child height when has one child" in {
      val instruction = mock[Step]
      when(instruction.height).thenReturn(2)
      val typeTree = new TypeTree(Seq(instruction))

      val height = typeTree.height

      height must equal(3)
    }

    "return 1 + child height when has two children" in {
      val instruction1 = mock[Step]
      when(instruction1.height).thenReturn(1)
      val instruction2 = mock[Step]
      when(instruction2.height).thenReturn(2)
      val typeTree = new TypeTree(Seq(instruction1, instruction2))

      val height = typeTree.height

      height must equal(3)
    }

    "return expected value for realistic tree" in {
      val typeTree = new TypeTree(
        Seq(
          Object(Seq(
            FunctionM(params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM())),
              nodes = Seq(
                AddOperator(
                  ValueRef("a"), ValueRef("b"))
              ), name = "f0")),
            name = "o0")))

      val height = typeTree.height

      height must equal(5)
    }
  }
}