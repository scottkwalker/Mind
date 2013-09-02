package nodes.helpers

import org.specs2.mutable._
import com.google.inject.Injector
import com.google.inject.Guice

class ScopeSpec extends Specification {
  "Scope" should {
    "defauls values to zero" in {
      Scope() must beLike {
        case Scope(numVals, accumulatorLength, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs) => {
          numVals mustEqual 0
          accumulatorLength mustEqual 0
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
        case Scope(numVals, accumulatorLength, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs) => {
          numVals mustEqual 1
          accumulatorLength mustEqual 0
          numFuncs mustEqual 0
          numObjects mustEqual 0
          stepsRemaining mustEqual 0
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
        }
      }
    }
    
    "incrementAccumulatorLength returns expected" in {
      Scope().incrementAccumulatorLength must beLike {
        case Scope(numVals, accumulatorLength, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs) => {
          numVals mustEqual 0
          accumulatorLength mustEqual 1
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
        case Scope(numVals, accumulatorLength, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs) => {
          numVals mustEqual 0
          accumulatorLength mustEqual 0
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
        case Scope(numVals, accumulatorLength, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs) => {
          numVals mustEqual 0
          accumulatorLength mustEqual 0
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
        case Scope(numVals, accumulatorLength, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs) => {
          numVals mustEqual 0
          accumulatorLength mustEqual 0
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
        incrementAccumulatorLength.
        incrementFuncs.
        incrementObjects.
        decrementStepsRemaining must beLike {
          case Scope(numVals, accumulatorLength, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncs) => {
            numVals mustEqual 1
            accumulatorLength mustEqual 1
            numFuncs mustEqual 1
            numObjects mustEqual 1
            stepsRemaining mustEqual 9
            maxExpressionsInFunc mustEqual 0
            maxFuncs mustEqual 0
          }
        }
    }
    
    "IoC creates a new instance with injected values" in {
      val injector: Injector = Guice.createInjector(new DevModule)
      val sut = injector.getInstance(classOf[Scope])

      sut.maxFuncsInObject mustEqual 10
      sut.maxExpressionsInFunc mustEqual 2
    }
    
    "funcHasSpaceForChildren true when there is space" in {
      Scope(accumulatorLength = 0, maxExpressionsInFunc = 1).funcHasSpaceForChildren mustEqual true
    } 
    
    "funcHasSpaceForChildren false when it reaches the max length" in {
      Scope(accumulatorLength = 1, maxExpressionsInFunc = 1).funcHasSpaceForChildren mustEqual false
    }
    
    "objHasSpaceForChildren true when there is space" in {
      Scope(accumulatorLength = 0, maxFuncsInObject = 1).objHasSpaceForChildren mustEqual true
    } 
    
    "objHasSpaceForChildren false when it reaches the max length" in {
      Scope(accumulatorLength = 1, maxFuncsInObject = 1).objHasSpaceForChildren mustEqual false
    }
  }
}