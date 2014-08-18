package nodes.helpers

import models.common.JsonValidationException
import nodes.legalNeighbours.BitSetAdapter.readJsonBitMaskToBitset
import play.api.libs.json.Json
import utils.helpers.UnitSpec
import scala.collection.immutable.BitSet

final class JsonDeserialiserSpec extends UnitSpec {

  "deserialize" should {
    "throws JsonValidationException when json is invalid" in {
      val invalidBitmaskJson = """{"bitMask":["INVALID"]}"""
      a[JsonValidationException] should be thrownBy JsonDeserialiser.deserialize(invalidBitmaskJson)
    }

    "return model when string is valid json" in {
      val dataAsBitSet = BitSet.empty + 3 + 4 + 4 + 100 + 101
      val bitMaskAsJson = """{"bitMask":[24,206158430208]}"""
      JsonDeserialiser.deserialize(bitMaskAsJson) should equal(dataAsBitSet)
    }

    "return model when JsValue is valid json" in {
      val dataAsBitSet = BitSet.empty + 3 + 4 + 4 + 100 + 101
      val bitMaskAsJson = Json.parse( """{"bitMask":[24,206158430208]}""")
      JsonDeserialiser.deserialize(bitMaskAsJson) should equal(dataAsBitSet)
    }
  }
}