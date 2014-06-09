package nodes.helpers

import utils.helpers.UnitSpec

final class IScopeSpec extends UnitSpec {
  "serialize" should {
    "return expected json" in {
      jsonSerialiser.serialize[IScope](asModel).toString should equal(asJson)
    }
  }

  "deserialize" should {
    "return expected model" in {
      jsonSerialiser.deserialize[IScope](asJson) should equal(asModel)
    }
  }

  val jsonSerialiser = new JsonSerialiser
  val asJson = """{"numVals":0,"numFuncs":0,"numObjects":0,"depth":0,"maxExpressionsInFunc":0,"maxFuncsInObject":0,"maxParamsInFunc":0,"maxDepth":0,"maxObjectsInTree":0}"""
  val asModel: IScope = Scope()
}