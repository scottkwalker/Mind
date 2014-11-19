package models.domain.scala

import com.google.inject.Injector
import composition.{StubReplaceEmpty, TestComposition}
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers._
import org.mockito.Mockito._

final class NodeTreeSpec extends TestComposition {

  "hasNoEmpty" must {
    "return true given it can terminates in under N steps" in {
      val s = Scope(height = 10)
      val f = ObjectDef(nodes = Seq.empty, name = "o0")
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.hasNoEmpty(s) must equal(true)
    }

    "return false given it cannot terminate in under N steps" in {
      val s = Scope(height = 10)
      val f = mock[Instruction]
      when(f.hasNoEmpty(any[Scope])).thenReturn(false)
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.hasNoEmpty(s) must equal(false)
    }

    "return true given none empty" in {
      val s = Scope(height = 10)
      val f = ObjectDef(nodes = Seq.empty, name = "o0")
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.hasNoEmpty(s) must equal(true)
    }

    "return false given empty root node" in {
      val s = Scope(height = 10)
      val nodeTree = new NodeTree(Seq(Empty()))
      nodeTree.hasNoEmpty(s) must equal(false)
    }

    "return false when hasHeightRemaining returns false" in {
      val s = mock[IScope]
      when(s.hasHeightRemaining).thenReturn(false)
      val f = mock[Instruction]
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.hasNoEmpty(s) must equal(false)
    }
  }

  "replaceEmpty" must {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val f = mock[Instruction]
      when(f.replaceEmpty(any[Scope])(any[Injector])).thenReturn(f)
      val instance = NodeTree(Seq(f))

      instance.replaceEmpty(s)

      verify(f, times(1)).replaceEmpty(any[Scope])(any[Injector])
    }

    "return same when no empty nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val f = mock[Instruction]
      when(f.replaceEmpty(any[Scope])(any[Injector])).thenReturn(f)
      val instance = new NodeTree(Seq(f))

      instance.replaceEmpty(s) must equal(instance)
    }

    "return without empty nodes given there were empty nodes" in {
      val s = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        height = 10,
        maxObjectsInTree = 1)
      val empty = Empty()
      val i = testInjector(new StubReplaceEmpty)
      val instance = NodeTree(nodes = Seq(empty))

      val result = instance.replaceEmpty(s)(i)

      result match {
        case NodeTree(nodes) =>
          nodes match {
            case Seq(n2) => n2 mustBe an[ObjectDef]
            case _ => fail("not a seq")
          }
        case _ => fail("wrong type")
      }
    }

    "throw when passed empty seq (no empty or non-empty)" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val instance = new NodeTree(nodes = Seq.empty)

      a[RuntimeException] must be thrownBy instance.replaceEmpty(s)
    }
  }

  "height" must {
    "return 1 + child height when has one child" in {
      val f = mock[Instruction]
      when(f.height).thenReturn(2)
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.height must equal(3)
    }

    "return 1 + child height when has two children" in {
      val f = mock[Instruction]
      when(f.height).thenReturn(1)
      val f2 = mock[Instruction]
      when(f2.height).thenReturn(2)
      val nodeTree = new NodeTree(Seq(f, f2))

      nodeTree.height must equal(3)
    }

    "return correct value for realistic tree" in {
      val nodeTree = new NodeTree(
        Seq(
          ObjectDef(Seq(
            FunctionM(params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM())),
              nodes = Seq(
                AddOperator(
                  ValueRef("a"), ValueRef("b"))
              ), name = "f0")),
            name = "o0")))

      nodeTree.height must equal(5)
    }
  }
}