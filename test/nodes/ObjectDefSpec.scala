package nodes

import nodes.helpers.{IScope, Scope}
import com.google.inject.{Guice, Injector}
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala.{Empty, FunctionM, ObjectDef}
import org.mockito.Mockito._
import org.mockito.Matchers._
import utils.helpers.UnitSpec

class ObjectDefSpec extends UnitSpec {
  "validate" should {
    "true given it can terminates in under N steps" in {
      val s = Scope(maxDepth = 4)
      val f = mock[FunctionM]
      when(f.validate(any[Scope])).thenReturn(true)
      val objectM = ObjectDef(Seq(f), name)

      objectM.validate(s) should equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val s = Scope(depth = 0)
      val f = mock[FunctionM]
      when(f.validate(any[Scope])).thenThrow(new RuntimeException)
      val objectM = ObjectDef(Seq(f), name)

      objectM.validate(s) should equal(false)
    }

    "false given it cannot terminate in under N steps" in {
      val s = Scope(depth = 3)
      val f = mock[FunctionM]
      when(f.validate(any[Scope])).thenReturn(false)
      val objectM = ObjectDef(Seq(f), name)

      objectM.validate(s) should equal(false)
    }

    "true given no empty nodes" in {
      val s = Scope(maxDepth = 10)
      val f = mock[FunctionM]
      when(f.validate(any[Scope])).thenReturn(true)
      val objectM = ObjectDef(Seq(f), name)

      objectM.validate(s) should equal(true)
    }

    "false given single empty method node" in {
      val s = Scope(maxDepth = 10)
      val objectM = ObjectDef(Seq(Empty()), name)
      objectM.validate(s) should equal(false)
    }

    "false given empty method node in a sequence" in {
      val s = Scope(maxDepth = 10)
      val f = mock[FunctionM]
      when(f.validate(any[Scope])).thenReturn(true)
      val objectM = ObjectDef(Seq(f, Empty()), name)

      objectM.validate(s) should equal(false)
    }
  }

  "toRawScala" should {
    "return expected" in {
      val f = mock[FunctionM]
      when(f.toRaw).thenReturn("STUB")
      val objectM = ObjectDef(Seq(f), name)

      objectM.toRaw should equal("object o0 { STUB }")
    }
  }

  "replaceEmpty" should {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      val f = mock[FunctionM]
      when(f.replaceEmpty(any[Scope], any[Injector])).thenReturn(f)
      val i = mock[Injector]
      val instance = ObjectDef(Seq(f), name = name)

      instance.replaceEmpty(s, i)

      verify(f, times(1)).replaceEmpty(any[Scope], any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[IScope]
      val f = mock[FunctionM]
      when(f.replaceEmpty(any[Scope], any[Injector])).thenReturn(f)
      val i = mock[Injector]
      val instance = ObjectDef(Seq(f), name)

      instance.replaceEmpty(s, i) should equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      val s = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        maxDepth = 5,
        maxObjectsInTree = 1)
      val n = mock[Empty]
      val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
      val instance = ObjectDef(nodes = Seq(n),
        name = name)

      val result = instance.replaceEmpty(s, injector)

      result match {
        case ObjectDef(n2, name2) =>
          n2 match {
            case Seq(nSeq) => nSeq shouldBe a[FunctionM]
            case _ => fail("not a Seq")
          }
          name2 should equal(name)
        case _ => fail("wrong type")
      }
    }
  }

  "getMaxDepth" should {
    "getMaxDepth returns 1 + child getMaxDepth when has 1 child" in {
      val f = mock[FunctionM]
      when(f.getMaxDepth).thenReturn(2)
      val objectM = ObjectDef(Seq(f), name)

      objectM.getMaxDepth should equal(3)
    }

    "getMaxDepth returns 1 + child getMaxDepth when has 2 children" in {
      val f = mock[FunctionM]
      when(f.getMaxDepth).thenReturn(1)
      val f2 = mock[FunctionM]
      when(f2.getMaxDepth).thenReturn(2)
      val objectM = ObjectDef(Seq(f, f2), name)

      objectM.getMaxDepth should equal(3)
    }
  }

  private final val name = "o0"
}