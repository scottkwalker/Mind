package serialization

import composition.TestHelpers
import play.api.libs.json.JsArray
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import serialization.BitSetAdapter.readJsonBitMaskToBitset

import scala.collection.immutable.BitSet

final class JsonDeserialiserSpec extends TestHelpers {

  "deserialize" must {
    "throws JsonValidationException when json is invalid" in {
      val invalidBitmaskJson = """{"bitMask":["INVALID"]}"""
      a[JsonValidationException] must be thrownBy JsonDeserialiser.deserialize(invalidBitmaskJson)
    }

    "return model when string is valid json" in {
      JsonDeserialiser.deserialize(asJson) must equal(dataAsBitSet)
    }

    "return model when JsValue is valid json" in {
      JsonDeserialiser.deserialize(asJson) must equal(dataAsBitSet)
    }
  }

  private val dataAsBitSet = BitSet.empty + 3 + 4 + 4 + 100 + 101
  private val asJson = JsObject(fields = Seq(("bitMask", JsArray(Seq(JsNumber(24), JsNumber(206158430208L))))))
}