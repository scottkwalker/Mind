package models.domain.scala

import com.google.inject.Injector
import composition.{StubReplaceEmpty, TestComposition}
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.{Await, Future}

final class FunctionMSpec extends TestComposition {

  "hasNoEmpty" must {
    "false given an empty name" in {
      val scope = Scope(height = 10)
      val node = mock[Instruction]
      when(node.hasNoEmpty(any[Scope])).thenThrow(new RuntimeException("should not have been called"))
      FunctionM(params = params,
        nodes = Seq(node, node),
        name = "").hasNoEmpty(scope) must equal(false)
    }

    "true given it can terminate in under N steps" in {
      val scope = Scope(height = 3)

      FunctionM(params = params,
        nodes = Seq(nonEmpty, nonEmpty),
        name = name).hasNoEmpty(scope) must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val scope = Scope(height = 0)
      val node = mock[Instruction]
      when(node.hasNoEmpty(any[Scope])).thenThrow(new RuntimeException("should not have been called"))

      FunctionM(params = params,
        nodes = Seq(node, node),
        name = name).hasNoEmpty(scope) must equal(false)
    }

    "false given it cannot terminate in under N steps" in {
      val scope = Scope(height = 2)
      val nonTerminal = mock[Instruction]
      when(nonTerminal.hasNoEmpty(any[Scope])).thenReturn(false)

      FunctionM(params = params,
        nodes = Seq(nonTerminal, nonTerminal),
        name = name).hasNoEmpty(scope) must equal(false)
    }

    "true given no empty nodes" in {
      val scope = Scope(height = 10)

      FunctionM(params = params,
        nodes = Seq(nonEmpty, nonEmpty),
        name = name).hasNoEmpty(scope) must equal(true)
    }

    "false given an empty node" in {
      val scope = Scope(height = 10)

      FunctionM(params = params,
        nodes = Seq(nonEmpty, Empty()),
        name = name).hasNoEmpty(scope) must equal(false)
    }
  }

  "toRawScala" must {
    "returns expected" in {
      val node = mock[Instruction]
      when(node.toRaw).thenReturn("STUB")

      FunctionM(params = params,
        nodes = Seq(node),
        name = name).toRaw must equal("def f0(a: Int, b: Int) = { STUB }")
    }

    "throws if has no name" in {
      val node = mock[Instruction]
      when(node.toRaw).thenReturn("STUB")
      val functionM = FunctionM(params = params,
        nodes = Seq(node),
        name = "")

      an[IllegalArgumentException] must be thrownBy functionM.toRaw
    }
  }

  "replaceEmpty" must {

    "calls replaceEmpty on non-empty child nodes" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val param = mock[Instruction]
      when(param.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(param)
      val node = mock[Instruction]
      when(node.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(node)
      val functionM = FunctionM(params = Seq(param),
        nodes = Seq(node),
        name = name)

      val result = functionM.replaceEmpty(scope)(injector)

      whenReady(result) { _ =>
        verify(param, times(1)).replaceEmpty(any[Scope])(any[Injector])
        verify(node, times(1)).replaceEmpty(any[Scope])(any[Injector])
      }
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val param = mock[Instruction]
      when(param.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(param)
      val node = mock[Instruction]
      when(node.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(node)
      val instance = FunctionM(params = Seq(param),
        nodes = Seq(node),
        name = name)

      val result = instance.replaceEmpty(scope)(injector)

      whenReady(result) {
        _ must equal(instance)
      }
    }

    "returns without empty nodes given there were empty nodes" in {
      val scope = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 5,
        maxObjectsInTree = 1)
      val empty = Empty()
      val injector = testInjector(new StubReplaceEmpty)
      val functionM = FunctionM(params = Seq(empty),
        nodes = Seq(Empty()),
        name = name)

      val result = functionM.replaceEmpty(scope)(injector)

      whenReady(result) {
        case FunctionM(p2, n2, n) =>
          p2 match {
            case Seq(pSeq) => pSeq mustBe a[ValDclInFunctionParam]
          }
          n2 match {
            case Seq(nSeq) => nSeq mustBe a[AddOperator]
          }
          n must equal(name)
      }
    }

    "throw when passed empty params seq (no empty or non-empty)" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val functionM = FunctionM(params = Seq.empty,
        nodes = Seq(Empty()),
        name = name)

      a[RuntimeException] must be thrownBy Await.result(functionM.replaceEmpty(scope)(injector), finiteTimeout)
    }

    "throw when passed empty nodes seq (no empty or non-empty)" in {
      val scope = mock[IScope]
      val injector = mock[Injector]
      val functionM = FunctionM(params = Seq(Empty()),
        nodes = Seq.empty,
        name = name)

      a[RuntimeException] must be thrownBy Await.result(functionM.replaceEmpty(scope)(injector), finiteTimeout)
    }
  }

  "height" must {
    "returns 1 + child height" in {
      val node = mock[Instruction]
      when(node.height) thenReturn 2

      FunctionM(params = params,
        nodes = Seq(node, node),
        name = name).height must equal(3)
    }

    "returns 1 + child height when children have different depths" in {
      val node1 = mock[Instruction]
      when(node1.height) thenReturn 1
      val node2 = mock[Instruction]
      when(node2.height) thenReturn 2

      FunctionM(params = params,
        nodes = Seq(node1, node2),
        name = name).height must equal(3)
    }
  }

  private val name = "f0"
  private val params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM()))

  private def nonEmpty = {
    val node = mock[Instruction]
    when(node.hasNoEmpty(any[Scope])).thenReturn(true)
    node
  }
}