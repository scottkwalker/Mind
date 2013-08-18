package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class ValueRefFactorySpec extends Specification with Mockito {
  "ValueRefFactorySpec" should {
    "create" in {
      "returns instance of this type" in {
        val s = mock[Scope]
        s.numVals returns 0
        
        ValueRefFactory().create(scope = s) must beAnInstanceOf[ValueRef]
      }

      "returns expected given scope with 0 vals" in {
        val s = mock[Scope]
        s.numVals returns 0

        ValueRefFactory().create(scope = s) must beLike {
          case ValueRef(name) => name mustEqual "v0"
        }
      }

      "returns expected given scope with 1 val" in {
        val s = mock[Scope]
        s.numVals returns 1

        ValueRefFactory().create(scope = s) must beLike {
          case ValueRef(name) => name mustEqual "v1"
        }
      }
    }
  }
}