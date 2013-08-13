package fitness

import org.specs2.mutable.Specification
import com.twitter.util.Eval
import fitness.Fitness.maxFitness
import org.specs2.mock.Mockito
import nodes._

class AdditionSpec extends Specification with Mockito {
  "Addition" should {
    "1 add 1 equals 2 with NodeTree that returns hard coded raw Scala" in {
      val nodeTree = mock[NodeTree]
      nodeTree.toRawScala returns "object Individual { def f0(a: Int, b: Int) = a + b }"
      val f = new AddTwoInts(nodeTree)
      f.fitness mustEqual maxFitness
    }

    "1 add 1 equals 2 with NodeTree that converts nodes to raw Scala" in {
      val nodeTree = new NodeTree(ObjectM(Seq(FunctionM(Seq(AddOperator(ValueRef("a"), ValueRef("b")))))))
      val f = new AddTwoInts(nodeTree)
      f.fitness mustEqual maxFitness
    }
  }
}