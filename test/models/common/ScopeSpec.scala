package models.common

import composition.UnitTestHelpers
import models.common.Scope.Form._
import play.api.libs.json._
import serialization.JsonDeserialiser

final class ScopeSpec extends UnitTestHelpers {

  "constructor" must {
    "set default values to zero" in {
      Scope() match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }

    "throw if height > maxHeight" in {
      a[RuntimeException] must be thrownBy Scope(height = 1, maxHeight = 0)
    }
  }

  "incrementVals" must {
    "return expected" in {
      Scope().incrementVals match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(1)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "incrementFuncs" must {
    "return expected" in {
      Scope().incrementFuncs match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(1)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "incrementObjects" must {
    "return expected" in {
      Scope().incrementObjects match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(0)
          numObjects must equal(1)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "decrementHeight" must {
    "return expected" in {
      Scope(height = 2, maxHeight = 10).decrementHeight match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(1)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(10)
        case _ => fail("must have matched")
      }
    }
  }

  "fluent interface" must {
    "return expected" in {
      Scope(height = 2, maxHeight = 10).
        incrementVals.
        incrementFuncs.
        incrementObjects.
        decrementHeight match {
          case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
            numVals must equal(1)
            numFuncs must equal(1)
            numObjects must equal(1)
            height must equal(1)
            maxExpressionsInFunc must equal(0)
            maxFuncs must equal(0)
            maxParamsInFunc must equal(0)
            maxObjectsInTree must equal(0)
            maxHeight must equal(10)
          case _ => fail("must have matched")
        }
    }
  }

  "setNumFuncs" must {
    "return expected" in {
      Scope().setNumFuncs(1) match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(1)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "setNumVals" must {
    "return expected" in {
      Scope().setNumVals(1) match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(1)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "serialize" must {
    "return expected json" in {
      Json.toJson(asModel) must equal(asJson)
    }
  }

  "deserialize" must {
    "return expected mode" in {
      JsonDeserialiser.deserialize[Scope](asJsonString) must equal(asModel)
    }
  }

  private val asJson = JsObject(
    fields = Seq(
      ("numVals", JsNumber(1)),
      ("numFuncs", JsNumber(2)),
      ("numObjects", JsNumber(3)),
      ("height", JsNumber(4)),
      ("maxExpressionsInFunc", JsNumber(5)),
      ("maxFuncsInObject", JsNumber(6)),
      ("maxParamsInFunc", JsNumber(7)),
      ("maxObjectsInTree", JsNumber(8)),
      (MaxHeightId, JsNumber(9))
    )
  )
  private val asJsonString = Json.stringify(asJson)
  private val asModel: IScope = Scope(
    numVals = 1,
    numFuncs = 2,
    numObjects = 3,
    height = 4,
    maxExpressionsInFunc = 5,
    maxFuncsInObject = 6,
    maxParamsInFunc = 7,
    maxObjectsInTree = 8,
    maxHeight = 9
  )
  private val asJsonWithType = JsObject(
    Seq(
      ("scopeContent", asJson)
    )
  )
}