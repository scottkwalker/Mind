package models.domain.scala

import composition.StubFactoryLookupBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Await
import scala.concurrent.Future

final class ObjectSpec extends UnitTestHelpers with TestComposition {

  "hasNoEmptySteps" must {
    "true given it can terminates in under N steps" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val functionM = mock[FunctionM]
      when(functionM.hasNoEmptySteps(any[IScope])).thenReturn(true)
      val objectDef = Object(Seq(functionM), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val scope = mock[IScope]
      val step = mock[Step]
      when(step.hasNoEmptySteps(any[Scope])).thenThrow(new RuntimeException)
      val objectDef = Object(Seq(step), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false given it cannot terminate in under N steps" in {
      val scope = Scope(height = 3, maxHeight = 10)
      val step = mock[Step]
      when(step.hasNoEmptySteps(any[Scope])).thenReturn(false)
      val objectDef = Object(Seq(step), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "true given no empty nodes" in {
      val scope = Scope(height = 10, maxHeight = 10)
      // This has to be a real instead of a mock as we will be exact-matching on the type.
      val instruction = FunctionMImpl(params = Seq.empty,
        nodes = Seq.empty,
        name = "f0")
      val objectDef = Object(Seq(instruction), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "false given single empty method node" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val objectDef = Object(Seq(Empty()), name)
      objectDef.hasNoEmptySteps(scope) must equal(false)
    }

    "false given empty method node in a sequence" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val instruction = mock[Step]
      when(instruction.hasNoEmptySteps(any[Scope])).thenReturn(true)
      val objectDef = Object(Seq(instruction, Empty()), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }
  }

  "toCompilable" must {
    "return expected" in {
      val instruction = mock[Step]
      when(instruction.toCompilable).thenReturn("STUB")
      val objectDef = Object(Seq(instruction), name)

      val compilable = objectDef.toCompilable

      compilable must equal("object o0 { STUB }")
    }
  }

  "fillEmptySteps" must {
    "calls fillEmptySteps on non-empty child nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val objectDef = Object(Seq(instruction), name = name)

      val fillEmptySteps = objectDef.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) { _ =>
        verify(instruction, times(1)).fillEmptySteps(any[Scope], any[FactoryLookup])
      }(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val objectDef = Object(Seq(instruction), name)

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
      val objectDef = Object(nodes = Seq(empty),
        name = name)

      val fillEmptySteps = objectDef.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) {
        case Object(n2, name2) =>
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
      val instance = new Object(nodes = Seq.empty, name = name)

      a[RuntimeException] must be thrownBy Await.result(instance.fillEmptySteps(scope, factoryLookup), finiteTimeout)
    }
  }

  "height" must {
    "height returns 1 + child height when has 1 child" in {
      val instruction = mock[Step]
      when(instruction.height).thenReturn(2)
      val objectDef = Object(Seq(instruction), name)

      val height = objectDef.height

      height must equal(3)
    }

    "height returns 1 + child height when has 2 children" in {
      val instruction1 = mock[Step]
      when(instruction1.height).thenReturn(1)
      val instruction2 = mock[Step]
      when(instruction2.height).thenReturn(2)
      val objectDef = Object(Seq(instruction1, instruction2), name)

      val height = objectDef.height

      height must equal(3)
    }
  }

  private val name = "o0"
}