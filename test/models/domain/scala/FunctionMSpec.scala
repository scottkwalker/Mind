package models.domain.scala

import com.google.inject.{Guice, Injector}
import models.domain.common.Node
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import nodes.helpers.{IScope, Scope}
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.helpers.UnitSpec

final class FunctionMSpec extends UnitSpec {
  "validate" should {
    "false given an empty name" in {
      val s = Scope(depth = 10)
      val v = mock[Node]
      when(v.validate(any[Scope])).thenReturn(true)
      FunctionM(params = params,
        nodes = Seq(v, v),
        name = "").validate(s) should equal(false)
    }

    "true given it can terminate in under N steps" in {
      val s = Scope(depth = 3)
      val v = mock[Node]
      when(v.validate(any[Scope])).thenReturn(true)

      FunctionM(params = params,
        nodes = Seq(v, v),
        name = name).validate(s) should equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val s = Scope(depth = 0)
      val v = mock[Node]
      when(v.validate(any[Scope])).thenThrow(new RuntimeException)

      FunctionM(params = params,
        nodes = Seq(v, v),
        name = name).validate(s) should equal(false)
    }

    "false given it cannot terminate in under N steps" in {
      val s = Scope(depth = 2)
      val v = mock[Node]
      when(v.validate(any[Scope])).thenReturn(false)

      FunctionM(params = params,
        nodes = Seq(v, v),
        name = name).validate(s) should equal(false)
    }

    "true given no empty nodes" in {
      val s = Scope(depth = 10)
      val v = mock[Node]
      when(v.validate(any[Scope])).thenReturn(true)

      FunctionM(params = params,
        nodes = Seq(v, v),
        name = name).validate(s) should equal(true)
    }

    "false given an empty node" in {
      val s = Scope(depth = 10)
      val v = mock[Node]
      when(v.validate(any[Scope])).thenReturn(true)

      FunctionM(params = params,
        nodes = Seq(v, Empty()),
        name = name).validate(s) should equal(false)
    }
  }

  "toRawScala" should {
    "returns expected" in {
      val a = mock[Node]
      when(a.toRaw).thenReturn("STUB")

      FunctionM(params = params,
        nodes = Seq(a),
        name = name).toRaw should equal("def f0(a: Int, b: Int) = { STUB }")
    }

    "throws if has no name" in {
      val a = mock[Node]
      when(a.toRaw).thenReturn("STUB")
      val sut = FunctionM(params = params,
        nodes = Seq(a),
        name = "")

      an[IllegalArgumentException] should be thrownBy sut.toRaw
    }
  }

  "replaceEmpty" should {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      val p = mock[Node]
      when(p.replaceEmpty(any[Scope], any[Injector])).thenReturn(p)
      val v = mock[Node]
      when(v.replaceEmpty(any[Scope], any[Injector])) thenReturn v
      val i = mock[Injector]
      val instance = FunctionM(params = Seq(p),
        nodes = Seq(v),
        name = name)

      instance.replaceEmpty(s, i)

      verify(p, times(1)).replaceEmpty(any[Scope], any[Injector])
      verify(v, times(1)).replaceEmpty(any[Scope], any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[IScope]
      val p = mock[Node]
      when(p.replaceEmpty(any[Scope], any[Injector])) thenReturn p
      val v = mock[Node]
      when(v.replaceEmpty(any[Scope], any[Injector])) thenReturn v
      val i = mock[Injector]
      val instance = FunctionM(params = Seq(p),
        nodes = Seq(v),
        name = name)

      instance.replaceEmpty(s, i) should equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      val s = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        depth = 5,
        maxObjectsInTree = 1)
      val empty = Empty()
      val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
      val instance = FunctionM(params = Seq(empty),
        nodes = Seq(Empty()),
        name = name)

      val result = instance.replaceEmpty(s, injector)

      result match {
        case FunctionM(p2, n2, n) =>
          p2 match {
            case Seq(pSeq) => pSeq shouldBe a[ValDclInFunctionParam]
          }
          n2 match {
            case Seq(nSeq) => nSeq shouldBe a[AddOperator]
          }
          n should equal(name)
      }
    }

    "getMaxDepth" should {
      "returns 1 + child getMaxDepth" in {
        val v = mock[Node]
        when(v.getMaxDepth) thenReturn 2

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).getMaxDepth should equal(3)
      }

      "returns 1 + child getMaxDepth when children have different depths" in {
        val v = mock[Node]
        when(v.getMaxDepth) thenReturn 1
        val v2 = mock[Node]
        when(v2.getMaxDepth) thenReturn 2

        FunctionM(params = params,
          nodes = Seq(v, v2),
          name = name).getMaxDepth should equal(3)
      }
    }
  }

  private final val name = "f0"
  private val params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM()))
}