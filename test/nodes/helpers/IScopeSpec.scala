package nodes.helpers

import models.common.{IScope, Scope}
import play.api.libs.json.{JsNumber, JsObject, Json}
import utils.helpers.UnitSpec

final class IScopeSpec extends UnitSpec {

  "serialize" should {
    "return expected json" in {
      Json.toJson(asModel) should equal(
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

  "deserialize" should {
    "return expected model" in {
      JsonDeserialiser.deserialize[IScope](asJson) should equal(asModel)
    }
  }

  val asJson = """{"numVals":0,"numFuncs":0,"numObjects":0,"height":0,"maxExpressionsInFunc":0,"maxFuncsInObject":0,"maxParamsInFunc":0,"height":0,"maxObjectsInTree":0}"""
  val asModel: IScope = Scope()
}