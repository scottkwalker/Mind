package nodes.helpers

import org.specs2.mutable._
import com.google.inject.Injector
import com.google.inject.Guice
import ai.aco.AcoModule

class ScopeSpec extends Specification {
  "Scope" should {
    "defauls values to zero" in {
      Scope() must beLike {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) => {
          numVals mustEqual 0
          numFuncs mustEqual 0
          numObjects mustEqual 0
          depth mustEqual 0
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
          maxParamsInFunc mustEqual 0
          maxDepth mustEqual 0
          maxObjectsInTree mustEqual 0
        }
      }
    }

    "incrementVals returns expected" in {
      Scope().incrementVals must beLike {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) => {
          numVals mustEqual 1
          numFuncs mustEqual 0
          numObjects mustEqual 0
          depth mustEqual 0
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
          maxParamsInFunc mustEqual 0
          maxDepth mustEqual 0
          maxObjectsInTree mustEqual 0
        }
      }
    }

    "incrementFuncs returns expected" in {
      Scope().incrementFuncs must beLike {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) => {
          numVals mustEqual 0
          numFuncs mustEqual 1
          numObjects mustEqual 0
          depth mustEqual 0
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
          maxParamsInFunc mustEqual 0
          maxDepth mustEqual 0
          maxObjectsInTree mustEqual 0
        }
      }
    }

    "incrementObjects returns expected" in {
      Scope().incrementObjects must beLike {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) => {
          numVals mustEqual 0
          numFuncs mustEqual 0
          numObjects mustEqual 1
          depth mustEqual 0
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
          maxParamsInFunc mustEqual 0
          maxDepth mustEqual 0
          maxObjectsInTree mustEqual 0
        }
      }
    }

    "decrementStepsRemaining returns expected" in {
      Scope(depth = 0).incrementDepth must beLike {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) => {
          numVals mustEqual 0
          numFuncs mustEqual 0
          numObjects mustEqual 0
          depth mustEqual 1
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
          maxParamsInFunc mustEqual 0
          maxDepth mustEqual 0
          maxObjectsInTree mustEqual 0
        }
      }
    }

    "fluent interface returns expected" in {
      Scope(depth = 0).
        incrementVals.
        incrementFuncs.
        incrementObjects.
        incrementDepth must beLike {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) => {
          numVals mustEqual 1
          numFuncs mustEqual 1
          numObjects mustEqual 1
          depth mustEqual 1
          maxExpressionsInFunc mustEqual 0
          maxFuncs mustEqual 0
          maxParamsInFunc mustEqual 0
          maxDepth mustEqual 0
          maxObjectsInTree mustEqual 0
        }
      }
    }

    "IoC creates a new instance with injected values" in {
      val injector: Injector = Guice.createInjector(new DevModule, new AcoModule)
      val sut = injector.getInstance(classOf[IScope])

      sut.maxFuncsInObject mustEqual 10
      sut.maxExpressionsInFunc mustEqual 2
    }
  }
}