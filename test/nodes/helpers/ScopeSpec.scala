package nodes.helpers

import com.google.inject.Injector
import com.google.inject.Guice
import modules.ai.aco.AcoModule
import modules.DevModule
import utils.helpers.UnitSpec

final class ScopeSpec extends UnitSpec {
  "constructor" should {
    "set default values to zero" in {
      Scope() match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(0)
          numFuncs should equal(0)
          numObjects should equal(0)
          depth should equal(0)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "incrementVals" should {
    "return expected" in {
      Scope().incrementVals match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(1)
          numFuncs should equal(0)
          numObjects should equal(0)
          depth should equal(0)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "incrementFuncs" should {
    "return expected" in {
      Scope().incrementFuncs match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(0)
          numFuncs should equal(1)
          numObjects should equal(0)
          depth should equal(0)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "incrementObjects" should {
    "return expected" in {
      Scope().incrementObjects match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(0)
          numFuncs should equal(0)
          numObjects should equal(1)
          depth should equal(0)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "decrementStepsRemaining" should {
    "return expected" in {
      Scope(depth = 0).incrementDepth match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(0)
          numFuncs should equal(0)
          numObjects should equal(0)
          depth should equal(1)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "fluent interface" should {
    "return expected" in {
      Scope(depth = 0).
        incrementVals.
        incrementFuncs.
        incrementObjects.
        incrementDepth match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(1)
          numFuncs should equal(1)
          numObjects should equal(1)
          depth should equal(1)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "IoC create" should {
    "return a new instance with injected values" in {
      val injector: Injector = Guice.createInjector(new DevModule, new AcoModule)
      val sut = injector.getInstance(classOf[IScope])

      sut.maxFuncsInObject should equal(10)
      sut.maxExpressionsInFunc should equal(2)
    }
  }

  "serialize" should {
    "return expected json" in {
      jsonSerialiser.serialize(asModel).toString should equal(asJson)
    }
  }

  "deserialize" should {
    "return expected mode" in {
      jsonSerialiser.deserialize(asJson) should equal(asModel)
    }
  }

  val jsonSerialiser = new JsonSerialiser
  val asJson = """{"numVals":0,"numFuncs":0,"numObjects":0,"depth":0,"maxExpressionsInFunc":0,"maxFuncsInObject":0,"maxParamsInFunc":0,"maxDepth":0,"maxObjectsInTree":0}"""
  val asModel: IScope = Scope()
}