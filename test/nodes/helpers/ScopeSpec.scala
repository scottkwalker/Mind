package nodes.helpers

import org.specs2.mutable._
import com.google.inject.Injector
import com.google.inject.Guice
import ai.aco.AcoModule

class ScopeSpec extends Specification {
  "Scope" should {
    "defauls values to zero" in {
      Scope() must beLike {
        case Scope(numVals, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs, maxParamsInFunc) => {
          numVals mustEqual 0
          numFuncs mustEqual 0
          numObjects mustEqual 0
          stepsRemaining mustEqual 0
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
        }
      }
    }

    "incrementVals returns expected" in {
      Scope().incrementVals must beLike {
        case Scope(numVals, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs, maxParamsInFunc) => {
          numVals mustEqual 1
          numFuncs mustEqual 0
          numObjects mustEqual 0
          stepsRemaining mustEqual 0
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
        }
      }
    }

    "incrementFuncs returns expected" in {
      Scope().incrementFuncs must beLike {
        case Scope(numVals, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs, maxParamsInFunc) => {
          numVals mustEqual 0
          numFuncs mustEqual 1
          numObjects mustEqual 0
          stepsRemaining mustEqual 0
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
        }
      }
    }

    "incrementObjects returns expected" in {
      Scope().incrementObjects must beLike {
        case Scope(numVals, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs, maxParamsInFunc) => {
          numVals mustEqual 0
          numFuncs mustEqual 0
          numObjects mustEqual 1
          stepsRemaining mustEqual 0
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
        }
      }
    }

    "decrementStepsRemaining returns expected" in {
      Scope(stepsRemaining = 10).decrementStepsRemaining must beLike {
        case Scope(numVals, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs, maxParamsInFunc) => {
          numVals mustEqual 0
          numFuncs mustEqual 0
          numObjects mustEqual 0
          stepsRemaining mustEqual 9
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
        }
      }
    }

    "fluent interface returns expected" in {
      Scope(stepsRemaining = 10).
        incrementVals.
        incrementFuncs.
        incrementObjects.
        decrementStepsRemaining must beLike {
        case Scope(numVals, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs, maxParamsInFunc) => {
          numVals mustEqual 1
          numFuncs mustEqual 1
          numObjects mustEqual 1
          stepsRemaining mustEqual 9
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
        }
      }
    }

    "IoC creates a new instance with injected values" in {
      val injector: Injector = Guice.createInjector(new DevModule, new AcoModule)
      val sut = injector.getInstance(classOf[Scope])

      sut.maxFuncsInObject mustEqual 10
      sut.maxExpressionsInFunc mustEqual 2
    }
  }
}