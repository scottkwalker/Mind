package models.domain.scala

import com.google.inject.Injector
import composition.{StubReplaceEmpty, TestComposition}
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.{Await, Future}

final class TypeTreeSpec extends TestComposition {

  "hasNoEmpty" must {
    "return true given it can terminates in under N steps" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val instruction = Object(nodes = Seq.empty, name = "o0")
      val typeTree = new TypeTree(Seq(instruction))

      typeTree.hasNoEmpty(scope) must equal(true)
    }

    "return false given it cannot terminate in under N steps" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val instruction = mock[Instruction]
      when(instruction.hasNoEmpty(any[Scope])).thenReturn(false)
      val typeTree = new TypeTree(Seq(instruction))

      typeTree.hasNoEmpty(scope) must equal(false)
    }

    "return true given none empty" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val instruction = Object(nodes = Seq.empty, name = "o0")
      val typeTree = new TypeTree(Seq(instruction))

      typeTree.hasNoEmpty(scope) must equal(true)
    }

    "return false given empty root node" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val typeTree = new TypeTree(Seq(Empty()))
      typeTree.hasNoEmpty(scope) must equal(false)
    }

    "return false when hasHeightRemaining returns false" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(false)
      val instruction = mock[Instruction]
      val typeTree = new TypeTree(Seq(instruction))

      typeTree.hasNoEmpty(scope) must equal(false)
    }
  }

  "replaceEmpty" must {
    "calls replaceEmpty on non-empty child nodes" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val instruction = mock[Instruction]
      when(instruction.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(instruction)
      val instance = TypeTree(Seq(instruction))

      val result = instance.replaceEmpty(scope)(injector)
      whenReady(result) { _ => verify(instruction, times(1)).replaceEmpty(any[Scope])(any[Injector])}(config = whenReadyPatienceConfig)
    }

    "return same when no empty nodes" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val instruction = mock[Instruction]
      when(instruction.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(instruction)
      val instance = new TypeTree(Seq(instruction))

      val result = instance.replaceEmpty(scope)(injector)

      whenReady(result) {
        _ must equal(instance)
      }(config = whenReadyPatienceConfig)
    }

    "return without empty nodes given there were empty nodes" in {
      val scope = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 10,
        maxObjectsInTree = 1,
        maxHeight = 10)
      val empty = Empty()
      val injector = testInjector(new StubReplaceEmpty)
      val instance = TypeTree(nodes = Seq(empty))

      val result = instance.replaceEmpty(scope)(injector)

      whenReady(result) {
        case TypeTree(nodes) =>
          nodes match {
            case Seq(nonEmpty) => nonEmpty mustBe an[Object]
            case _ => fail("not a seq")
          }
        case _ => fail("wrong type")
      }(config = whenReadyPatienceConfig)
    }

    "throw when passed empty seq (no empty or non-empty)" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val instance = new TypeTree(nodes = Seq.empty)

      a[RuntimeException] must be thrownBy Await.result(instance.replaceEmpty(scope)(injector), finiteTimeout)
    }
  }

  "height" must {
    "return 1 + child height when has one child" in {
      val instruction = mock[Instruction]
      when(instruction.height).thenReturn(2)
      val typeTree = new TypeTree(Seq(instruction))

      typeTree.height must equal(3)
    }

    "return 1 + child height when has two children" in {
      val instruction1 = mock[Instruction]
      when(instruction1.height).thenReturn(1)
      val instruction2 = mock[Instruction]
      when(instruction2.height).thenReturn(2)
      val typeTree = new TypeTree(Seq(instruction1, instruction2))

      typeTree.height must equal(3)
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

      typeTree.height must equal(5)
    }
  }
}