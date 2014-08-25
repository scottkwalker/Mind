package models.domain.scala

import com.google.inject.Injector
import composition.TestComposition
import models.common.{IScope, Scope}
import models.domain.Node
import org.mockito.Matchers._
import org.mockito.Mockito._

final class ObjectDefSpec extends TestComposition {

  "validate" must {
    "true given it can terminates in under N steps" in {
      val s = Scope(height = 4)
      val f = FunctionM(params = Seq.empty,
        nodes = Seq.empty,
        name = "f0")
      val objectM = ObjectDef(Seq(f), name)

      objectM.validate(s) must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val s = Scope(height = 0)
      val f = mock[Node]
      when(f.validate(any[Scope])).thenThrow(new RuntimeException)
      val objectM = ObjectDef(Seq(f), name)

      objectM.validate(s) must equal(false)
    }

    "false given it cannot terminate in under N steps" in {
      val s = Scope(height = 3)
      val f = mock[Node]
      when(f.validate(any[Scope])).thenReturn(false)
      val objectM = ObjectDef(Seq(f), name)

      objectM.validate(s) must equal(false)
    }

    "true given no empty nodes" in {
      val s = Scope(height = 10)
      val f = FunctionM(params = Seq.empty,
        nodes = Seq.empty,
        name = "f0")
      val objectM = ObjectDef(Seq(f), name)

      objectM.validate(s) must equal(true)
    }

    "false given single empty method node" in {
      val s = Scope(height = 10)
      val objectM = ObjectDef(Seq(Empty()), name)
      objectM.validate(s) must equal(false)
    }

    "false given empty method node in a sequence" in {
      val s = Scope(height = 10)
      val f = mock[Node]
      when(f.validate(any[Scope])).thenReturn(true)
      val objectM = ObjectDef(Seq(f, Empty()), name)

      objectM.validate(s) must equal(false)
    }
  }

  "toRawScala" must {
    "return expected" in {
      val f = mock[Node]
      when(f.toRaw).thenReturn("STUB")
      val objectM = ObjectDef(Seq(f), name)

      objectM.toRaw must equal("object o0 { STUB }")
    }
  }

  "replaceEmpty" must {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val f = mock[Node]
      when(f.replaceEmpty(any[Scope])(any[Injector])).thenReturn(f)
      val instance = ObjectDef(Seq(f), name = name)

      instance.replaceEmpty(s)

      verify(f, times(1)).replaceEmpty(any[Scope])(any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val f = mock[Node]
      when(f.replaceEmpty(any[Scope])(any[Injector])).thenReturn(f)
      val instance = ObjectDef(Seq(f), name)

      instance.replaceEmpty(s) must equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      val s = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 5,
        maxObjectsInTree = 1)
      val empty = Empty()
      val instance = ObjectDef(nodes = Seq(empty),
        name = name)

      val result = instance.replaceEmpty(s)(injector)

      result match {
        case ObjectDef(n2, name2) =>
          n2 match {
            case Seq(nSeq) => nSeq mustBe a[FunctionM]
            case _ => fail("not a Seq")
          }
          name2 must equal(name)
        case _ => fail("wrong type")
      }
    }
  }

  "getMaxDepth" must {
    "getMaxDepth returns 1 + child getMaxDepth when has 1 child" in {
      val f = mock[Node]
      when(f.getMaxDepth).thenReturn(2)
      val objectM = ObjectDef(Seq(f), name)

      objectM.getMaxDepth must equal(3)
    }

    "getMaxDepth returns 1 + child getMaxDepth when has 2 children" in {
      val f = mock[Node]
      when(f.getMaxDepth).thenReturn(1)
      val f2 = mock[Node]
      when(f2.getMaxDepth).thenReturn(2)
      val objectM = ObjectDef(Seq(f, f2), name)

      objectM.getMaxDepth must equal(3)
    }
  }

  private final val name = "o0"
}