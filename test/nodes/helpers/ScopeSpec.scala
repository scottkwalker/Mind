package nodes.helpers

import org.specs2.mutable._

class ScopeSpec extends Specification {
  "Scope" should {
    "defauls values to zero" in {
      Scope() must beLike {
        case Scope(v, f, o) => {
          v mustEqual 0
          f mustEqual 0
          o mustEqual 0
        }
      }
    }

    "incrementVals returns expected" in {
      Scope().incrementVals must beLike {
        case Scope(v, f, o) => {
          v mustEqual 1
          f mustEqual 0
          o mustEqual 0
        }
      }
    }

    "incrementFuncs returns expected" in {
      Scope().incrementFuncs must beLike {
        case Scope(v, f, o) => {
          v mustEqual 0
          f mustEqual 1
          o mustEqual 0
        }
      }
    }

    "incrementObjects returns expected" in {
      Scope().incrementObjects must beLike {
        case Scope(v, f, o) => {
          v mustEqual 0
          f mustEqual 0
          o mustEqual 1
        }
      }
    }
  }
}