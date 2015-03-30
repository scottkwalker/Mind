package models.domain.scala

import composition.{StubFactoryLookupBindingBuilder, StubFactoryLookupAnyBinding, TestComposition, UnitTestHelpers}
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future
import utils.ScopeHelper._

final class ObjectSpec extends UnitTestHelpers with TestComposition {

  "hasNoEmptySteps" must {
    "return true if it can terminates in under N steps" in {
      val step = mock[FunctionM]
      when(step.hasNoEmptySteps(any[IScope])).thenReturn(true)
      val objectDef = ObjectImpl(Seq(step), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(true)
    }

    "return false if the scope does not have height remaining" in {
      val step = mock[Step]
      when(step.hasNoEmptySteps(any[IScope])).thenThrow(new RuntimeException("test should not get this far"))
      val objectDef = ObjectImpl(Seq(step), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scopeWithoutHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "return false if the nodes cannot terminate in under N steps" in {
      val step = mock[FunctionM]
      when(step.hasNoEmptySteps(any[IScope])).thenReturn(false)
      val objectDef = ObjectImpl(Seq(step), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "return false if there is a single empty node" in {
      val objectDef = ObjectImpl(Seq(Empty()), name)
      objectDef.hasNoEmptySteps(scopeWithHeightRemaining) must equal(false)
    }

    "return false if there is an empty node in a sequence" in {
      val instruction = mock[FunctionM]
      when(instruction.hasNoEmptySteps(any[IScope])).thenReturn(true)
      val objectDef = ObjectImpl(Seq(instruction, Empty()), name)

      val hasNoEmptySteps = objectDef.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "throw an exception if there is a node of an unhandled node type" in {
      val unhandledNode = mock[Step]
      val objectImpl = new ObjectImpl(nodes = Seq(unhandledNode), name = "o0")

      a[RuntimeException] must be thrownBy objectImpl.hasNoEmptySteps(scopeWithHeightRemaining)
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
    "call fillEmptySteps and updateScope once on non-empty child nodes" in {
      val factoryLookup = mock[FactoryLookup]
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val objectDef = ObjectImpl(Seq(instruction), name = name)

      val fillEmptySteps = objectDef.fillEmptySteps(scope(), factoryLookup)

      whenReady(fillEmptySteps) { _ =>
        verify(instruction, times(1)).fillEmptySteps(any[IScope], any[FactoryLookup])
        verify(instruction, times(1)).updateScope(any[IScope])
        verifyNoMoreInteractions(instruction)
      }(config = patienceConfig)
    }

    "return the same if there are no empty nodes" in {
      val factoryLookup = mock[FactoryLookup]
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val objectDef = ObjectImpl(Seq(instruction), name)

      val fillEmptySteps = objectDef.fillEmptySteps(scope(), factoryLookup)

      whenReady(fillEmptySteps) {
        _ must equal(objectDef)
      }(config = patienceConfig)
    }

    "return without empty nodes if there were empty nodes" in {
      val scope = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 5,
        maxObjectsInTree = 1,
        maxHeight = 10)
      val empty = Empty()
      val factoryLookup = testInjector(new StubFactoryLookupBindingBuilder().withGenericDecision).getInstance(classOf[FactoryLookup])
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

    "throw an exception if nodes list is empty (no empty or non-empty)" in {
      val factoryLookup = mock[FactoryLookup]
      val instance = new ObjectImpl(nodes = Seq.empty, name = name)

      a[RuntimeException] must be thrownBy instance.fillEmptySteps(scope(), factoryLookup).futureValue
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