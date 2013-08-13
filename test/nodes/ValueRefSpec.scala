package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import nodes.helpers.Scope

class ValueRefSpec extends Specification {
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
        val scope = Scope()
        
        ValueRef.create(scope = scope) must beAnInstanceOf[ValueRef]
      }

      "returns expected given scope with 0 vals" in {
        val scope = Scope()

        ValueRef.create(scope = scope) must beLike {
          case ValueRef(name) => name mustEqual "v0"
        }
      }

      "returns expected given scope with 1 val" in {
        val scope = Scope().incrementVals

        ValueRef.create(scope = scope) must beLike {
          case ValueRef(name) => name mustEqual "v1"
        }
      }
    }
  }
}