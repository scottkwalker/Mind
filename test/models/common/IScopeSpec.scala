package models.common

import composition.TestComposition
import play.api.libs.json.{JsNumber, JsObject, Json}
import serialization.JsonDeserialiser

final class IScopeSpec extends TestComposition {

  "serialize" must {
    "return expected json" in {
      Json.toJson(asModel) must equal(
        JsObject(
          Seq(
            ("numVals", JsNumber(0)),
            ("numFuncs", JsNumber(0)),
            ("numObjects", JsNumber(0)),
            ("height", JsNumber(0)),
            ("maxExpressionsInFunc", JsNumber(0)),
            ("maxFuncsInObject", JsNumber(0)),
            ("maxParamsInFunc", JsNumber(0)),
            ("height", JsNumber(0)),
            ("maxObjectsInTree", JsNumber(0))
          )
        )
      )
    }
  }

  "deserialize" must {
    "return expected model when input is a JsValue" in {
      JsonDeserialiser.deserialize[IScope](asJson) must equal(asModel)
    }

    "return expected model when input is a String" in {
      JsonDeserialiser.deserialize[IScope](asString) must equal(asModel)
    }
  }

  private val asJson = JsObject(
    fields = Seq(
      ("numVals", JsNumber(0)),
      ("numFuncs", JsNumber(0)),
      ("numObjects", JsNumber(0)),
      ("height", JsNumber(0)),
      ("maxExpressionsInFunc", JsNumber(0)),
      ("maxFuncsInObject", JsNumber(0)),
      ("maxParamsInFunc", JsNumber(0)),
      ("maxObjectsInTree", JsNumber(0))
    )
  )
  private val asString = Json.stringify(asJson)
  private val asModel: IScope = Scope()
}