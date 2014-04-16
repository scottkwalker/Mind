package nodes

import org.specs2.mutable._
import org.specs2.mock.Mockito
import java.lang.IllegalArgumentException
import com.google.inject.{Guice, Injector}
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala._
import nodes.helpers.Scope
import models.domain.scala.ValDclInFunctionParam
import models.domain.scala.AddOperator
import models.domain.scala.IntegerM
import models.domain.scala.FunctionM

class FuncDefSpec extends Specification with Mockito {
  "FuncDef" should {
    val name = "f0"
    val params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM()))

    "validate" in {
      "false given an empty name" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true
        FunctionM(params = params,
          nodes = Seq(v, v),
          name = "").validate(s) mustEqual false
      }

      "true given it can terminate in under N steps" in {
        val s = Scope(maxDepth = 3)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).validate(s) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val s = Scope(depth = 0)
        val v = mock[ValueRef]
        v.validate(any[Scope]) throws new RuntimeException

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).validate(s) mustEqual false
      }

      "false given it cannot terminate in under N steps" in {
        val s = Scope(depth = 2)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns false

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).validate(s) mustEqual false
      }

      "true given no empty nodes" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).validate(s) mustEqual true
      }

      "false given an empty node" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        FunctionM(params = params,
          nodes = Seq(v, Empty()),
          name = name).validate(s) mustEqual false
      }
    }

    "toRawScala" in {
      "returns expected" in {
        val a = mock[ValueRef]
        a.toRaw returns "STUB"

        FunctionM(params = params,
          nodes = Seq(a),
          name = name).toRaw mustEqual "def f0(a: Int, b: Int) = { STUB }"
      }

      "throws if has no name" in {
        val a = mock[ValueRef]
        a.toRaw returns "STUB"

        FunctionM(params = params,
          nodes = Seq(a),
          name = "").toRaw must throwA[IllegalArgumentException]
      }
    }

    "replaceEmpty" in {
      "calls replaceEmpty on non-empty child nodes" in {
        val s = mock[Scope]
        val p = mock[ValDclInFunctionParam]
        p.replaceEmpty(any[Scope], any[Injector]) returns p
        val v = mock[ValueRef]
        v.replaceEmpty(any[Scope], any[Injector]) returns v
        val i = mock[Injector]
        val instance = FunctionM(params = Seq(p),
          nodes = Seq(v),
          name = name)

        instance.replaceEmpty(s, i)

        there was one(p).replaceEmpty(any[Scope], any[Injector])
        there was one(v).replaceEmpty(any[Scope], any[Injector])
      }

      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val p = mock[ValDclInFunctionParam]
        p.replaceEmpty(any[Scope], any[Injector]) returns p
        val v = mock[ValueRef]
        v.replaceEmpty(any[Scope], any[Injector]) returns v
        val i = mock[Injector]
        val instance = FunctionM(params = Seq(p),
          nodes = Seq(v),
          name = name)

        instance.replaceEmpty(s, i) mustEqual instance
      }

      "returns without empty nodes given there were empty nodes" in {
        val s = Scope(maxExpressionsInFunc = 1,
          maxFuncsInObject = 1,
          maxParamsInFunc = 1,
          maxDepth = 5,
          maxObjectsInTree = 1)
        val p = mock[Empty]
        val v = mock[Empty]
        val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
        val instance = FunctionM(params = Seq(p),
          nodes = Seq(v),
          name = name)

        val result = instance.replaceEmpty(s, injector)

        result must beLike {
          case FunctionM(p2, n2, n) =>
            p2 must beLike {
              case Seq(pSeq) => pSeq must beAnInstanceOf[ValDclInFunctionParam]
            }
            n2 must beLike {
              case Seq(nSeq) => nSeq must beAnInstanceOf[AddOperator]
            }
            n mustEqual name
        }
      }

      "getMaxDepth" in {
        "returns 1 + child getMaxDepth" in {
          val v = mock[ValueRef]
          v.getMaxDepth returns 2

          FunctionM(params = params,
            nodes = Seq(v, v),
            name = name).getMaxDepth mustEqual 3
        }

        "returns 1 + child getMaxDepth when children have different depths" in {
          val v = mock[ValueRef]
          v.getMaxDepth returns 1
          val v2 = mock[ValueRef]
          v2.getMaxDepth returns 2

          FunctionM(params = params,
            nodes = Seq(v, v2),
            name = name).getMaxDepth mustEqual 3
        }
      }
    }
  }
}