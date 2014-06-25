package nodes.helpers

import utils.helpers.UnitSpec
import play.api.libs.json.{Json, JsNumber, JsObject}

final class IScopeSpec extends UnitSpec {
  "serialize" should {
    "return expected json" in {
      Json.toJson(asModel) should equal(
        JsObject(
          Seq(
            ("numVals", JsNumber(0)),
            ("numFuncs", JsNumber(0)),
            ("numObjects", JsNumber(0)),
            ("depth", JsNumber(0)),
            ("maxExpressionsInFunc", JsNumber(0)),
            ("maxFuncsInObject", JsNumber(0)),
            ("maxParamsInFunc", JsNumber(0)),
            ("maxDepth", JsNumber(0)),
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

  val asJson = """{"numVals":0,"numFuncs":0,"numObjects":0,"depth":0,"maxExpressionsInFunc":0,"maxFuncsInObject":0,"maxParamsInFunc":0,"maxDepth":0,"maxObjectsInTree":0}"""
  val asModel: IScope = Scope()
}