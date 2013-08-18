package nodes

import org.specs2.mutable.Specification
import com.twitter.util.Eval
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope

class NodeTreeSpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTree" should {
    "validate" in {
      "true given it can terminates in under N steps" in {
        val f = mock[ObjectM]
        f.validate(anyInt) returns true 
        val nodeTree = new NodeTree(f)
        
        nodeTree.validate(5) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        val f = mock[ObjectM]
        f.validate(anyInt) returns false 
        val nodeTree = new NodeTree(f)
        
        nodeTree.validate(4) mustEqual false
      }

      "true given none empty" in {
        val f = mock[ObjectM]
        f.validate(anyInt) returns true 
        val nodeTree = new NodeTree(f)
        
        nodeTree.validate(10) mustEqual true
      }

      "false given empty root node" in {
        val nodeTree = new NodeTree(Empty())
        nodeTree.validate(10) mustEqual false
      }
    }
  }
}