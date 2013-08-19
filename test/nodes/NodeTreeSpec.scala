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
        val s = Scope(stepsRemaining = 5)
        val f = mock[ObjectM]
        f.validate(any[Scope]) returns true 
        val nodeTree = new NodeTree(f)
        
        nodeTree.validate(s) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        val s = Scope(stepsRemaining = 5)
        val f = mock[ObjectM]
        f.validate(any[Scope]) returns false 
        val nodeTree = new NodeTree(f)
        
        nodeTree.validate(s) mustEqual false
      }

      "true given none empty" in {
        val s = Scope(stepsRemaining = 10)
        val f = mock[ObjectM]
        f.validate(any[Scope]) returns true 
        val nodeTree = new NodeTree(f)
        
        nodeTree.validate(s) mustEqual true
      }

      "false given empty root node" in {
        val s = Scope(stepsRemaining = 10)
        val nodeTree = new NodeTree(Empty())
        nodeTree.validate(s) mustEqual false
      }
    }
  }
}