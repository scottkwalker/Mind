package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class ValueRefSpec extends Specification with Mockito {
  "ValueRef" should {
    "toRawScala" in {
      ValueRef("a").toRawScala mustEqual "a"
    }

    "validate" in {
      "true given it can terminates in under N steps" in {
        ValueRef("a").validate(1) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        ValueRef("a").validate(0) mustEqual false
      }

      "true given a non-empty name" in {
        ValueRef("a").validate(10) mustEqual true
      }

      "false given an empty name" in {
        ValueRef("").validate(10) mustEqual false
      }
    }

    "create" in {
      "returns instance of this type" in {
        val s = mock[Scope]
        s.numVals returns 0
        
        ValueRef.create(scope = s) must beAnInstanceOf[ValueRef]
      }

      "returns expected given scope with 0 vals" in {
        val s = mock[Scope]
        s.numVals returns 0

        ValueRef.create(scope = s) must beLike {
          case ValueRef(name) => name mustEqual "v0"
        }
      }

      "returns expected given scope with 1 val" in {
        val s = mock[Scope]
        s.numVals returns 1

        ValueRef.create(scope = s) must beLike {
          case ValueRef(name) => name mustEqual "v1"
        }
      }
    }
  }
}