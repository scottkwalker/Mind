package nodes

import org.specs2.mutable.Specification
import com.twitter.util.Eval
import nodes._
import org.specs2.mock.Mockito

class NodeTreeSpec extends Specification with Mockito {
  "NodeTree" should {
    "return expected when rawScala called" in {
      val nodeTree = new NodeTree(ObjectM())

      nodeTree.toRawScala mustEqual "object Individual { def f1(a: Int, b: Int) = { a + b } }"
    }
  }
}