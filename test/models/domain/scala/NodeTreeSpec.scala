package models.domain.scala

import com.google.inject.Injector
import composition.TestComposition
import models.common.{IScope, Scope}
import models.domain.Node
import org.mockito.Matchers._
import org.mockito.Mockito._

final class NodeTreeSpec extends TestComposition {

  "validate" must {
    "true given it can terminates in under N steps" in {
      val s = Scope(height = 10)
      val f = ObjectDef(nodes = Seq.empty, name = "o0")
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) must equal(true)
    }

    "false given it cannot terminate in under N steps" in {
      val s = Scope(height = 10)
      val f = mock[Node]
      when(f.validate(any[Scope])).thenReturn(false)
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) must equal(false)
    }

    "true given none empty" in {
      val s = Scope(height = 10)
      val f = ObjectDef(nodes = Seq.empty, name = "o0")
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) must equal(true)
    }

    "false given empty root node" in {
      val s = Scope(height = 10)
      val nodeTree = new NodeTree(Seq(Empty()))
      nodeTree.validate(s) must equal(false)
    }

    "false when hasHeightRemaining returns false" in {
      val s = mock[IScope]
      when(s.hasHeightRemaining).thenReturn(false)
      val f = mock[Node]
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) must equal(false)
    }
  }

  "replaceEmpty" must {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val f = mock[Node]
      when(f.replaceEmpty(any[Scope])(any[Injector])).thenReturn(f)
      val instance = NodeTree(Seq(f))

      instance.replaceEmpty(s)

      verify(f, times(1)).replaceEmpty(any[Scope])(any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val f = mock[Node]
      when(f.replaceEmpty(any[Scope])(any[Injector])).thenReturn(f)
      val instance = new NodeTree(Seq(f))

      instance.replaceEmpty(s) must equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      val s = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 10,
        maxObjectsInTree = 1)
      val empty = Empty()
      val instance = NodeTree(nodes = Seq(empty))

      val result = instance.replaceEmpty(s)(injector)

      result match {
        case NodeTree(nodes) =>
          nodes match {
            case Seq(n2) => n2 mustBe an[ObjectDef]
            case _ => fail("not a seq")
          }
        case _ => fail("wrong type")
      }
    }
  }

  "getMaxDepth" must {
    "returns 1 + child getMaxDepth when has one child" in {
      val f = mock[Node]
      when(f.getMaxDepth).thenReturn(2)
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.getMaxDepth must equal(3)
    }

    "returns 1 + child getMaxDepth when has two children" in {
      val f = mock[Node]
      when(f.getMaxDepth).thenReturn(1)
      val f2 = mock[Node]
      when(f2.getMaxDepth).thenReturn(2)
      val nodeTree = new NodeTree(Seq(f, f2))

      nodeTree.getMaxDepth must equal(3)
    }

    "returns correct value for realistic tree" in {
      val nodeTree = new NodeTree(
        Seq(
          ObjectDef(Seq(
            FunctionM(params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM())),
              nodes = Seq(
                AddOperator(
                  ValueRef("a"), ValueRef("b"))
              ), name = "f0")),
            name = "o0")))

      nodeTree.getMaxDepth must equal(5)
    }
  }
}