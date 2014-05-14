package nodes

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import com.google.inject.{Guice, Injector}
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala._
import models.domain.scala.AddOperator
import nodes.helpers.Scope
import models.domain.scala.IntegerM
import models.domain.scala.ObjectDef
import models.domain.scala.FunctionM

class NodeTreeSpec extends Specification with Mockito with PendingUntilFixed {
  "validate" in {
    "true given it can terminates in under N steps" in {
      val s = Scope(maxDepth = 10)
      val f = mock[ObjectDef]
      f.validate(any[Scope]) returns true
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) mustEqual true
    }

    "false given it cannot terminate in under N steps" in {
      val s = Scope(maxDepth = 10)
      val f = mock[ObjectDef]
      f.validate(any[Scope]) returns false
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) mustEqual false
    }

    "true given none empty" in {
      val s = Scope(maxDepth = 10)
      val f = mock[ObjectDef]
      f.validate(any[Scope]) returns true
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.validate(s) mustEqual true
    }

    "false given empty root node" in {
      val s = Scope(maxDepth = 10)
      val nodeTree = new NodeTree(Seq(Empty()))
      nodeTree.validate(s) mustEqual false
    }
  }

  "replaceEmpty" in {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[Scope]
      val f = mock[ObjectDef]
      f.replaceEmpty(any[Scope], any[Injector]) returns f
      val i = mock[Injector]
      val instance = NodeTree(Seq(f))

      instance.replaceEmpty(s, i)

      there was one(f).replaceEmpty(any[Scope], any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[Scope]
      val f = mock[ObjectDef]
      f.replaceEmpty(any[Scope], any[Injector]) returns f
      val i = mock[Injector]
      val instance = new NodeTree(Seq(f))

      instance.replaceEmpty(s, i) mustEqual instance
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

      result must beLike {
        case NodeTree(nodes) =>
          nodes must beLike {
            case Seq(n2) => n2 must beAnInstanceOf[ObjectDef]
          }
      }
    }
  }

  "getMaxDepth" in {
    "returns 1 + child getMaxDepth" in {
      val f = mock[ObjectDef]
      f.getMaxDepth returns 2
      val nodeTree = new NodeTree(Seq(f))

      nodeTree.getMaxDepth mustEqual 3
    }

    "returns 1 + child getMaxDepth" in {
      val f = mock[ObjectDef]
      f.getMaxDepth returns 1
      val f2 = mock[ObjectDef]
      f2.getMaxDepth returns 2
      val nodeTree = new NodeTree(Seq(f, f2))

      nodeTree.getMaxDepth mustEqual 3
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

      nodeTree.getMaxDepth mustEqual 5
    }
  }
}