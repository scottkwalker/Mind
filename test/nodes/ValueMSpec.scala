package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import nodes.helpers.Scope

class ValueMSpec extends Specification {
  "ValueM" should {
    "toRawScala" in {
      ValueM("a").toRawScala mustEqual "a"
    }

    "validate" in {
      "true given it can terminates in under N steps" in {
        ValueM("a").validate(1) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        ValueM("a").validate(0) mustEqual false
      }

      "true given a non-empty name" in {
        ValueM("a").validate(10) mustEqual true
      }

      "false given an empty name" in {
        ValueM("").validate(10) mustEqual false
      }
    }

    "create" in {
      "returns instance of this type" in {
        val scope = Scope(numVals = 0)
        ValueM.create(scope = None) must beAnInstanceOf[ValueM]
      }

      "returns expected given scope with 0 vals" in {
        val scope = Scope(numVals = 0)

        ValueM.create(scope = Some(scope)) must beLike {
          case ValueM(name) => name mustEqual "v0"
        }
      }

      "returns expected given scope with 1 val" in {
        val scope = Scope(numVals = 1)

        ValueM.create(scope = Some(scope)) must beLike {
          case ValueM(name) => name mustEqual "v1"
        }
      }
    }
  }
}