package nodes

import com.google.inject.{Guice, Injector}
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala._
import models.domain.scala.AddOperator
import nodes.helpers.{IScope, Scope}
import models.domain.scala.IntegerM
import models.domain.scala.ObjectDef
import models.domain.scala.FunctionM
import org.mockito.Mockito._
import org.mockito.Matchers._
import utils.helpers.UnitSpec

final class NodeTreeSpec extends UnitSpec {
  "validate" should {
    "true given it can terminates in under N steps" in {
      val s = Scope(maxDepth = 10)
      val f = mock[ObjectDef]
      when(f.validate(any[Scope])).thenReturn(true)
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) should equal(true)
    }

    "false given it cannot terminate in under N steps" in {
      val s = Scope(maxDepth = 10)
      val f = mock[ObjectDef]
      when(f.validate(any[Scope])).thenReturn(false)
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) should equal(false)
    }

    "true given none empty" in {
      val s = Scope(maxDepth = 10)
      val f = mock[ObjectDef]
      when(f.validate(any[Scope])).thenReturn(true)
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) should equal(true)
    }

    "false given empty root node" in {
      val s = Scope(maxDepth = 10)
      val nodeTree = new NodeTree(Seq(Empty()))
      nodeTree.validate(s) should equal(false)
    }
  }

  "replaceEmpty" should {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      val f = mock[ObjectDef]
      when(f.replaceEmpty(any[Scope], any[Injector])).thenReturn(f)
      val i = mock[Injector]
      val instance = NodeTree(Seq(f))

      instance.replaceEmpty(s, i)

      verify(f, times(1)).replaceEmpty(any[Scope], any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[IScope]
      val f = mock[ObjectDef]
      when(f.replaceEmpty(any[Scope], any[Injector])).thenReturn(f)
      val i = mock[Injector]
      val instance = new NodeTree(Seq(f))

      instance.replaceEmpty(s, i) should equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      val s = Scope(maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 1,
        maxDepth = 10,
        maxObjectsInTree = 1)
      val n = mock[Empty]
      val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
      val instance = NodeTree(nodes = Seq(n))

      val result = instance.replaceEmpty(s, injector)

      result match {
        case NodeTree(nodes) =>
          nodes match {
            case Seq(n2) => n2 shouldBe an[ObjectDef]
            case _ => fail("not a seq")
          }
        case _ => fail("wrong type")
      }
    }
  }

  "getMaxDepth" should {
    "returns 1 + child getMaxDepth when has one child" in {
      val f = mock[ObjectDef]
      when(f.getMaxDepth).thenReturn(2)
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.getMaxDepth should equal(3)
    }

    "returns 1 + child getMaxDepth when has two children" in {
      val f = mock[ObjectDef]
      when(f.getMaxDepth).thenReturn(1)
      val f2 = mock[ObjectDef]
      when(f2.getMaxDepth).thenReturn(2)
      val nodeTree = new NodeTree(Seq(f, f2))

      nodeTree.getMaxDepth should equal(3)
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

      nodeTree.getMaxDepth should equal(5)
    }
  }
}