package nodes

import org.specs2.mutable.Specification
import com.twitter.util.Eval
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope

class NodeTreeFactorySpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTreeFactory" should {
    "create returns instance of this type" in {
      NodeTreeFactory().create(scope = Scope()) must beAnInstanceOf[NodeTree]
    }
  }
}