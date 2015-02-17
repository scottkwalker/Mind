package models.domain.scala

import composition.UnitTestHelpers
import composition.TestComposition
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Await
import scala.concurrent.Future

final class FunctionMSpec extends UnitTestHelpers with TestComposition {

  "hasNoEmptySteps" must {
    "false given an empty name" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val node = mock[Step]
      when(node.hasNoEmptySteps(any[Scope])).thenThrow(new RuntimeException("hasNoEmpty should not have been called"))

      val hasNoEmptySteps = FunctionM(params = params,
        nodes = Seq(node, node),
        name = "").hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "true given it can terminate in under N steps" in {
      val scope = Scope(height = 3, maxHeight = 10)

      val hasNoEmptySteps = FunctionM(params = params,
        nodes = Seq(nonEmpty, nonEmpty),
        name = name).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val scope = Scope(height = 0)
      val node = mock[Step]
      when(node.hasNoEmptySteps(any[Scope])).thenThrow(new RuntimeException("hasNoEmpty should not have been called"))

      val hasNoEmptySteps = FunctionM(params = params,
        nodes = Seq(node, node),
        name = name).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false given it cannot terminate in under N steps" in {
      val scope = Scope(height = 2, maxHeight = 10)
      val nonTerminal = mock[Step]
      when(nonTerminal.hasNoEmptySteps(any[Scope])).thenReturn(false)

      val hasNoEmptySteps = FunctionM(params = params,
        nodes = Seq(nonTerminal, nonTerminal),
        name = name).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "true given no empty nodes" in {
      val scope = Scope(height = 10, maxHeight = 10)

      val hasNoEmptySteps = FunctionM(params = params,
        nodes = Seq(nonEmpty, nonEmpty),
        name = name).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "false given an empty node" in {
      val scope = Scope(height = 10, maxHeight = 10)

      val hasNoEmptySteps = FunctionM(params = params,
        nodes = Seq(nonEmpty, Empty()),
        name = name).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }
  }

  "toCompilable" must {
    "returns expected" in {
      val node = mock[Step]
      when(node.toCompilable).thenReturn("STUB")
      val functionM = FunctionM(params = params,
        nodes = Seq(node),
        name = name)

      val toCompilable = functionM.toCompilable

      toCompilable must equal("def f0(a: Int, b: Int) = { STUB }")
    }

    "throws if has no name" in {
      val node = mock[Step]
      when(node.toCompilable).thenReturn("STUB")
      val functionM = FunctionM(params = params,
        nodes = Seq(node),
        name = "")

      an[IllegalArgumentException] must be thrownBy functionM.toCompilable
    }
  }

  "fillEmptySteps" must {

    "calls fillEmptySteps on non-empty child nodes" in {
      val scope = mock[IScope]
      val factoryLookup = testInjector().getInstance(classOf[FactoryLookup])
      val param = mock[Step]
      when(param.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(param)
      val node = mock[Step]
      when(node.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(node)
      val functionM = FunctionM(params = Seq(param),
        nodes = Seq(node),
        name = name)

      val step = functionM.fillEmptySteps(scope, factoryLookup)

      whenReady(step) { _ =>
        verify(param, times(1)).fillEmptySteps(any[Scope], any[FactoryLookup])
        verify(node, times(1)).fillEmptySteps(any[Scope], any[FactoryLookup])
      }(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val factoryLookup = testInjector().getInstance(classOf[FactoryLookup])
      val param = mock[Step]
      when(param.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(param)
      val node = mock[Step]
      when(node.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(node)
      val instance = FunctionM(params = Seq(param),
        nodes = Seq(node),
        name = name)

      val result = instance.fillEmptySteps(scope, factoryLookup)

      whenReady(result) {
        _ must equal(instance)
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
      val factoryLookup = testInjector().getInstance(classOf[FactoryLookup])
      val functionM = FunctionM(params = Seq(empty),
        nodes = Seq(Empty()),
        name = name)

      val result = functionM.fillEmptySteps(scope, factoryLookup)

      whenReady(result) {
        case FunctionM(p2, n2, n) =>
          p2 match {
            case Seq(pSeq) => pSeq mustBe a[Step]
          }
          n2 match {
            case Seq(nSeq) => nSeq mustBe a[Step]
          }
          n must equal(name)
        case unknown => fail(s"unknown type: $unknown")
      }(config = patienceConfig)
    }

    "throw when passed empty params seq (no empty or non-empty)" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val functionM = FunctionM(params = Seq.empty,
        nodes = Seq(Empty()),
        name = name)

      a[RuntimeException] must be thrownBy Await.result(functionM.fillEmptySteps(scope, factoryLookup), finiteTimeout)
    }

    "throw when passed empty nodes seq (no empty or non-empty)" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val functionM = FunctionM(params = Seq(Empty()),
        nodes = Seq.empty,
        name = name)

      a[RuntimeException] must be thrownBy Await.result(functionM.fillEmptySteps(scope, factoryLookup), finiteTimeout)
    }
  }

  "height" must {
    "returns 1 + child height" in {
      val node = mock[Step]
      when(node.height) thenReturn 2
      val functionM = FunctionM(params = params,
        nodes = Seq(node, node),
        name = name)

      val height = functionM.height

      height must equal(3)
    }

    "returns 1 + child height when children have different depths" in {
      val node1 = mock[Step]
      when(node1.height) thenReturn 1
      val node2 = mock[Step]
      when(node2.height) thenReturn 2
      val functionM = FunctionM(params = params,
        nodes = Seq(node1, node2),
        name = name)

      val height = functionM.height

      height must equal(3)
    }
  }

  private val name = "f0"
  private val params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM()))

  private def nonEmpty = {
    val node = mock[Step]
    when(node.hasNoEmptySteps(any[Scope])).thenReturn(true)
    node
  }
}