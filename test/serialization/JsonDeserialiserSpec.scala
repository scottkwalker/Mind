package serialization

import composition.TestComposition
import play.api.libs.json.Json
import serialization.BitSetAdapter.readJsonBitMaskToBitset
import scala.collection.immutable.BitSet

final class JsonDeserialiserSpec extends TestComposition {

  "deserialize" must {
    "throws JsonValidationException when json is invalid" in {
      val invalidBitmaskJson = """{"bitMask":["INVALID"]}"""
      a[JsonValidationException] must be thrownBy JsonDeserialiser.deserialize(invalidBitmaskJson)
    }

    "return model when string is valid json" in {
      val dataAsBitSet = BitSet.empty + 3 + 4 + 4 + 100 + 101
      val bitMaskAsJson = """{"bitMask":[24,206158430208]}"""
      JsonDeserialiser.deserialize(bitMaskAsJson) must equal(dataAsBitSet)
    }

    "return model when JsValue is valid json" in {
      val dataAsBitSet = BitSet.empty + 3 + 4 + 4 + 100 + 101
      val bitMaskAsJson = Json.parse( """{"bitMask":[24,206158430208]}""")
      JsonDeserialiser.deserialize(bitMaskAsJson) must equal(dataAsBitSet)
    }
  }
}