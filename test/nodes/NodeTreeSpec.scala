package nodes

import org.specs2.mutable.Specification
import com.twitter.util.Eval
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed

class NodeTreeSpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTree" should {
    "is not terminal" in {
      val nodeTree = new NodeTree(ObjectM(Seq(Method(Seq(AddOperator(Value("a"), Value("b")))))))
      nodeTree.isTerminal mustEqual false
    }
    
    "canTerminate in 4 steps" in {
      val nodeTree = new NodeTree(ObjectM(Seq(Method(Seq(AddOperator(Value("a"), Value("b")))))))
      nodeTree.canTerminate(4) mustEqual true
    }
    
    "can not terminate in 3 steps" in {
      val nodeTree = new NodeTree(ObjectM(Seq(Method(Seq(AddOperator(Value("a"), Value("b")))))))
      nodeTree.canTerminate(3) mustEqual false
    }
        
    "toRawScala" in {
      val nodeTree = new NodeTree(ObjectM(Seq(Method(Seq(AddOperator(Value("a"), Value("b")))))))
      nodeTree.toRawScala mustEqual "object Individual { def f1(a: Int, b: Int) = { a + b } }"
    }
    
    "validate true when does not contain any empty nodes" in {
      val nodeTree = new NodeTree(ObjectM(Seq(Method(Seq(AddOperator(Value("a"), Value("b")))))))
      nodeTree.validate mustEqual true
    }
    
    "validate false given" in {
      "empty root node" in {
        val nodeTree = new NodeTree(Empty())
        nodeTree.validate mustEqual false
      }
      
      "single empty method node" in {
        val nodeTree = new NodeTree(ObjectM(Seq(Empty())))
        nodeTree.validate mustEqual false
      }
      
      "empty method node in a sequence" in {
        val nodeTree = new NodeTree(ObjectM(Seq(Method(Seq(AddOperator(Value("a"), Value("b")))), Empty())))
        nodeTree.validate mustEqual false
      }
    }
  }
}