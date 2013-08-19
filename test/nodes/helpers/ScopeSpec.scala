package nodes.helpers

import org.specs2.mutable._

class ScopeSpec extends Specification {
  "Scope" should {
    "defauls values to zero" in {
      Scope() must beLike {
        case Scope(v, f, o, s) => {
          v mustEqual 0
          f mustEqual 0
          o mustEqual 0
          s mustEqual 0
        }
      }
    }

    "incrementVals returns expected" in {
      Scope().incrementVals must beLike {
        case Scope(v, f, o, s) => {
          v mustEqual 1
          f mustEqual 0
          o mustEqual 0
          s mustEqual 0
        }
      }
    }

    "incrementFuncs returns expected" in {
      Scope().incrementFuncs must beLike {
        case Scope(v, f, o, s) => {
          v mustEqual 0
          f mustEqual 1
          o mustEqual 0
          s mustEqual 0
        }
      }
    }

    "incrementObjects returns expected" in {
      Scope().incrementObjects must beLike {
        case Scope(v, f, o, s) => {
          v mustEqual 0
          f mustEqual 0
          o mustEqual 1
          s mustEqual 0
        }
      }
    }

    "decrementStepsRemaining returns expected" in {
      Scope(stepsRemaining = 10).decrementStepsRemaining must beLike {
        case Scope(v, f, o, s) => {
          v mustEqual 0
          f mustEqual 0
          o mustEqual 0
          s mustEqual 9
        }
      }
    }

    "fluent interface returns expected" in {
      Scope(stepsRemaining = 10).
        incrementVals.
        incrementFuncs.
        incrementObjects.
        decrementStepsRemaining must beLike {
          case Scope(v, f, o, s) => {
            v mustEqual 1
            f mustEqual 1
            o mustEqual 1
            s mustEqual 9
          }
        }
    }
  }
}