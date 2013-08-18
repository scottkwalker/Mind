package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class AddOperatorFactorySpec extends Specification with Mockito {
  "AddOperatorFactory" should {
    "create returns instance of this type" in {
      val s = mock[Scope]
      s.numVals returns 0

      AddOperatorFactory().create(scope = s) must beAnInstanceOf[AddOperator]
    }
  }
}