package models.domain.scala

import com.google.inject.Injector
import composition.{StubReplaceEmpty, TestComposition}
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.{Await, Future}

final class ObjectSpec extends TestComposition {

  "hasNoEmpty" must {
    "true given it can terminates in under N steps" in {
      val scope = Scope(height = 4, maxHeight = 10)
      // This has to be a real instead of a mock as we will be exact-matching on the type.
      val instruction = FunctionM(params = Seq.empty,
        nodes = Seq.empty,
        name = "f0")
      val objectDef = Object(Seq(instruction), name)

      objectDef.hasNoEmpty(scope) must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val scope = Scope(height = 0)
      val instruction = mock[Instruction]
      when(instruction.hasNoEmpty(any[Scope])).thenThrow(new RuntimeException)
      val objectDef = Object(Seq(instruction), name)

      objectDef.hasNoEmpty(scope) must equal(false)
    }

    "false given it cannot terminate in under N steps" in {
      val scope = Scope(height = 3, maxHeight = 10)
      val instruction = mock[Instruction]
      when(instruction.hasNoEmpty(any[Scope])).thenReturn(false)
      val objectDef = Object(Seq(instruction), name)

      objectDef.hasNoEmpty(scope) must equal(false)
    }

    "true given no empty nodes" in {
      val scope = Scope(height = 10, maxHeight = 10)
      // This has to be a real instead of a mock as we will be exact-matching on the type.
      val instruction = FunctionM(params = Seq.empty,
        nodes = Seq.empty,
        name = "f0")
      val objectDef = Object(Seq(instruction), name)

      objectDef.hasNoEmpty(scope) must equal(true)
    }

    "false given single empty method node" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val objectDef = Object(Seq(Empty()), name)
      objectDef.hasNoEmpty(scope) must equal(false)
    }

    "false given empty method node in a sequence" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val instruction = mock[Instruction]
      when(instruction.hasNoEmpty(any[Scope])).thenReturn(true)
      val objectDef = Object(Seq(instruction, Empty()), name)

      objectDef.hasNoEmpty(scope) must equal(false)
    }
  }

  "toRawScala" must {
    "return expected" in {
      val instruction = mock[Instruction]
      when(instruction.toRaw).thenReturn("STUB")
      val objectDef = Object(Seq(instruction), name)

      objectDef.toRaw must equal("object o0 { STUB }")
    }
  }

  "replaceEmpty" must {
    "calls replaceEmpty on non-empty child nodes" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val instruction = mock[Instruction]
      when(instruction.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(instruction)
      val objectDef = Object(Seq(instruction), name = name)

      val result = objectDef.replaceEmpty(scope)(injector)

      whenReady(result) { r => verify(instruction, times(1)).replaceEmpty(any[Scope])(any[Injector])}(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val instruction = mock[Instruction]
      when(instruction.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(instruction)
      val objectDef = Object(Seq(instruction), name)

      val result = objectDef.replaceEmpty(scope)(injector)

      whenReady(result) {
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
      val injector = testInjector(new StubReplaceEmpty)
      val objectDef = Object(nodes = Seq(empty),
        name = name)

      val result = objectDef.replaceEmpty(scope)(injector)

      whenReady(result) {
        case Object(n2, name2) =>
          n2 match {
            case Seq(nSeq) => nSeq mustBe a[Instruction]
            case _ => fail("not a Seq")
          }
          name2 must equal(name)
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "throw when passed empty seq (no empty or non-empty)" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val instance = new Object(nodes = Seq.empty, name = name)

      a[RuntimeException] must be thrownBy Await.result(instance.replaceEmpty(scope)(injector), finiteTimeout)
    }
  }

  "height" must {
    "height returns 1 + child height when has 1 child" in {
      val instruction = mock[Instruction]
      when(instruction.height).thenReturn(2)
      val objectDef = Object(Seq(instruction), name)

      objectDef.height must equal(3)
    }

    "height returns 1 + child height when has 2 children" in {
      val instruction1 = mock[Instruction]
      when(instruction1.height).thenReturn(1)
      val instruction2 = mock[Instruction]
      when(instruction2.height).thenReturn(2)
      val objectDef = Object(Seq(instruction1, instruction2), name)

      objectDef.height must equal(3)
    }
  }

  private val name = "o0"
}