package fitness

import org.specs2.mutable.Specification
import com.twitter.util.Eval
import fitness.Fitness.maxFitness
import nodes.NodeTree

class AdditionSpec extends Specification {
  "Addition" should {
    "1 add 1 equals 2" in {
      val nodeTree = new NodeTree
      val f: Fitness = new Addition(nodeTree)
      f.fitness mustEqual maxFitness
    }
  }
}