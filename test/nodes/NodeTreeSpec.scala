package nodes

import org.specs2.mutable.Specification
import com.twitter.util.Eval
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed

class NodeTreeSpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTree" should {
    "validate" in {
      "true given it can terminates in under N steps" in {
        val nodeTree = new NodeTree(ObjectM(Seq(FunctionM(Seq(AddOperator(ValueM("a"), ValueM("b")))))))
        nodeTree.validate(5) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        val nodeTree = new NodeTree(ObjectM(Seq(FunctionM(Seq(AddOperator(ValueM("a"), ValueM("b")))))))
        nodeTree.validate(4) mustEqual false
      }

      "true given none empty" in {
        val nodeTree = new NodeTree(ObjectM(Seq(FunctionM(Seq(AddOperator(ValueM("a"), ValueM("b")))))))
        nodeTree.validate(10) mustEqual true
      }

      "false given empty root node" in {
        val nodeTree = new NodeTree(Empty())
        nodeTree.validate(10) mustEqual false
      }

      "false given single empty method node" in {
        val nodeTree = new NodeTree(ObjectM(Seq(Empty())))
        nodeTree.validate(10) mustEqual false
      }

      "false given empty method node in a sequence" in {
        val nodeTree = new NodeTree(ObjectM(Seq(FunctionM(Seq(AddOperator(ValueM("a"), ValueM("b")))), Empty())))
        nodeTree.validate(10) mustEqual false
      }
    }
    
    "create returns instance of this type" in {
      NodeTree.create(scope = None) must beAnInstanceOf[NodeTree]
    }
  }
}