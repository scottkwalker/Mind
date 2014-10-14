package models.domain.scala

import com.google.inject.Injector
import composition.{StubReplaceEmpty, TestComposition}
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers._
import org.mockito.Mockito._

final class FunctionMSpec extends TestComposition {

  "hasNoEmpty" must {
    "false given an empty name" in {
      val s = Scope(height = 10)
      val v = mock[Instruction]
      when(v.hasNoEmpty(any[Scope])).thenReturn(true)
      FunctionM(params = params,
        nodes = Seq(v, v),
        name = "").hasNoEmpty(s) must equal(false)
    }

    "true given it can terminate in under N steps" in {
      val s = Scope(height = 3)
      val v = mock[Instruction]
      when(v.hasNoEmpty(any[Scope])).thenReturn(true)

      FunctionM(params = params,
        nodes = Seq(v, v),
        name = name).hasNoEmpty(s) must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val s = Scope(height = 0)
      val v = mock[Instruction]
      when(v.hasNoEmpty(any[Scope])).thenThrow(new RuntimeException)

      FunctionM(params = params,
        nodes = Seq(v, v),
        name = name).hasNoEmpty(s) must equal(false)
    }

    "false given it cannot terminate in under N steps" in {
      val s = Scope(height = 2)
      val v = mock[Instruction]
      when(v.hasNoEmpty(any[Scope])).thenReturn(false)

      FunctionM(params = params,
        nodes = Seq(v, v),
        name = name).hasNoEmpty(s) must equal(false)
    }

    "true given no empty nodes" in {
      val s = Scope(height = 10)
      val v = mock[Instruction]
      when(v.hasNoEmpty(any[Scope])).thenReturn(true)

      FunctionM(params = params,
        nodes = Seq(v, v),
        name = name).hasNoEmpty(s) must equal(true)
    }

    "false given an empty node" in {
      val s = Scope(height = 10)
      val v = mock[Instruction]
      when(v.hasNoEmpty(any[Scope])).thenReturn(true)

      FunctionM(params = params,
        nodes = Seq(v, Empty()),
        name = name).hasNoEmpty(s) must equal(false)
    }
  }

  "toRawScala" must {
    "returns expected" in {
      val a = mock[Instruction]
      when(a.toRaw).thenReturn("STUB")

      FunctionM(params = params,
        nodes = Seq(a),
        name = name).toRaw must equal("def f0(a: Int, b: Int) = { STUB }")
    }

    "throws if has no name" in {
      val a = mock[Instruction]
      when(a.toRaw).thenReturn("STUB")
      val sut = FunctionM(params = params,
        nodes = Seq(a),
        name = "")

      an[IllegalArgumentException] must be thrownBy sut.toRaw
    }
  }

  "replaceEmpty" must {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val p = mock[Instruction]
      when(p.replaceEmpty(any[Scope])(any[Injector])).thenReturn(p)
      val v = mock[Instruction]
      when(v.replaceEmpty(any[Scope])(any[Injector])) thenReturn v
      val instance = FunctionM(params = Seq(p),
        nodes = Seq(v),
        name = name)

      instance.replaceEmpty(s)

      verify(p, times(1)).replaceEmpty(any[Scope])(any[Injector])
      verify(v, times(1)).replaceEmpty(any[Scope])(any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val p = mock[Instruction]
      when(p.replaceEmpty(any[Scope])(any[Injector])) thenReturn p
      val v = mock[Instruction]
      when(v.replaceEmpty(any[Scope])(any[Injector])) thenReturn v
      val instance = FunctionM(params = Seq(p),
        nodes = Seq(v),
        name = name)

      instance.replaceEmpty(s) must equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      val s = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 5,
        maxObjectsInTree = 1)
      val empty = Empty()
      val i = testInjector(new StubReplaceEmpty)
      val instance = FunctionM(params = Seq(empty),
        nodes = Seq(Empty()),
        name = name)

      val result = instance.replaceEmpty(s)(i)

      result match {
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

    "height" must {
      "returns 1 + child height" in {
        val v = mock[Instruction]
        when(v.height) thenReturn 2

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).height must equal(3)
      }

      "returns 1 + child height when children have different depths" in {
        val v = mock[Instruction]
        when(v.height) thenReturn 1
        val v2 = mock[Instruction]
        when(v2.height) thenReturn 2

        FunctionM(params = params,
          nodes = Seq(v, v2),
          name = name).height must equal(3)
      }
    }
  }

  private final val name = "f0"
  private val params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM()))
}