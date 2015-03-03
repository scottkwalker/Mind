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

final class ObjectSpec extends UnitTestHelpers with TestComposition {

  "hasNoEmptySteps" must {
    "true given it can terminates in under N steps" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val step = mock[FunctionM]
      when(step.hasNoEmptySteps(any[IScope])).thenReturn(true)
      val objectDef = ObjectImpl(Seq(step), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "false when scope does not have height remaining" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(false)
      val step = mock[Step]
      when(step.hasNoEmptySteps(any[IScope])).thenThrow(new RuntimeException("test should not get this far"))
      val objectDef = ObjectImpl(Seq(step), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false when nodes cannot terminate in under N steps" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val step = mock[FunctionM]
      when(step.hasNoEmptySteps(any[IScope])).thenReturn(false)
      val objectDef = ObjectImpl(Seq(step), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false given single empty method node" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val objectDef = ObjectImpl(Seq(Empty()), name)
      objectDef.hasNoEmptySteps(scope) must equal(false)
    }

    "false given empty method node in a sequence" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val instruction = mock[FunctionM]
      when(instruction.hasNoEmptySteps(any[IScope])).thenReturn(true)
      val objectDef = ObjectImpl(Seq(instruction, Empty()), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "throw when there is a node of an unhandled node type" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val unhandledNode = mock[Step]
      val objectImpl = new ObjectImpl(nodes = Seq(unhandledNode), name = "o0")

      a[RuntimeException] must be thrownBy objectImpl.hasNoEmptySteps(scope)
    }
  }

  "toCompilable" must {
    "return expected" in {
      val instruction = mock[Step]
      when(instruction.toCompilable).thenReturn("STUB")
      val objectDef = ObjectImpl(Seq(instruction), name)

      val compilable = objectDef.toCompilable

      compilable must equal("object o0 { STUB }")
    }
  }

  "fillEmptySteps" must {
    "calls fillEmptySteps on non-empty child nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val objectDef = ObjectImpl(Seq(instruction), name = name)

      val fillEmptySteps = objectDef.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) { _ =>
        verify(instruction, times(1)).fillEmptySteps(any[IScope], any[FactoryLookup])
      }(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val objectDef = ObjectImpl(Seq(instruction), name)

      val fillEmptySteps = objectDef.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) {
        _ must equal(objectDef)
      }(config = patienceConfig)
    }

    "returns without empty nodes given there were empty nodes" in {
      val scope = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 5,
        maxObjectsInTree = 1,
        maxHeight = 10)
      val empty = Empty()
      val factoryLookup = testInjector(new StubFactoryLookupBinding).getInstance(classOf[FactoryLookup])
      val objectDef = ObjectImpl(nodes = Seq(empty),
        name = name)

      val fillEmptySteps = objectDef.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) {
        case ObjectImpl(n2, name2) =>
          n2 match {
            case Seq(nSeq) => nSeq mustBe a[Step]
            case _ => fail("not a Seq")
          }
          name2 must equal(name)
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "throw when passed empty seq (no empty or non-empty)" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val instance = new ObjectImpl(nodes = Seq.empty, name = name)

      a[RuntimeException] must be thrownBy instance.fillEmptySteps(scope, factoryLookup).futureValue
    }
  }

  "height" must {
    "height returns 1 + child height when has 1 child" in {
      val instruction = mock[Step]
      when(instruction.height).thenReturn(2)
      val objectDef = ObjectImpl(Seq(instruction), name)

      val height = objectDef.height

      height must equal(3)
    }

    "height returns 1 + child height when has 2 children" in {
      val instruction1 = mock[Step]
      when(instruction1.height).thenReturn(1)
      val instruction2 = mock[Step]
      when(instruction2.height).thenReturn(2)
      val objectDef = ObjectImpl(Seq(instruction1, instruction2), name)

      val height = objectDef.height

      height must equal(3)
    }
  }

  private val name = "o0"
}