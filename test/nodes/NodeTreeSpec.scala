package nodes

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope
import com.google.inject.Injector

class NodeTreeSpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTree" should {
    "validate" in {
      "true given it can terminates in under N steps" in {
        val s = Scope(maxDepth = 10)
        val f = mock[ObjectM]
        f.validate(any[Scope]) returns true
        val nodeTree = new NodeTree(Seq(f))

        nodeTree.validate(s) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        val s = Scope(maxDepth = 10)
        val f = mock[ObjectM]
        f.validate(any[Scope]) returns false
        val nodeTree = new NodeTree(Seq(f))

        nodeTree.validate(s) mustEqual false
      }

      "true given none empty" in {
        val s = Scope(maxDepth = 10)
        val f = mock[ObjectM]
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
      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val f = mock[ObjectM]
        val i = mock[Injector]
        val instance = new NodeTree(Seq(f))

        instance.replaceEmpty(s, i) mustEqual instance
      }
    }
  }
}