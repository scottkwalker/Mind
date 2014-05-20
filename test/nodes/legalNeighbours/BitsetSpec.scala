package nodes.legalNeighbours

import nodes.helpers.{BinarySerializer, JsonSerialiser}
import scala.collection.immutable.BitSet
import utils.helpers.UnitSpec

final class BitSetSpec extends UnitSpec {
  private val dataAsBitSet = BitSet.empty + 3 + 4 + 4 + 100 + 101
  private val dataAsArrayByte = Array[Byte](-84, -19, 0, 5, 115, 114, 0, 41, 115, 99, 97, 108, 97, 46, 99, 111, 108, 108, 101, 99, 116, 105, 111, 110, 46, 105, 109, 109, 117, 116, 97, 98, 108, 101, 46, 66, 105, 116, 83, 101, 116, 36, 66, 105, 116, 83, 101, 116, 50, 80, -120, 62, 83, -86, 18, -99, 16, 2, 0, 2, 74, 0, 6, 101, 108, 101, 109, 115, 48, 74, 0, 6, 101, 108, 101, 109, 115, 49, 120, 114, 0, 33, 115, 99, 97, 108, 97, 46, 99, 111, 108, 108, 101, 99, 116, 105, 111, 110, 46, 105, 109, 109, 117, 116, 97, 98, 108, 101, 46, 66, 105, 116, 83, 101, 116, 22, 92, -7, 50, -51, -22, -58, -54, 2, 0, 0, 120, 112, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 48, 0, 0, 0, 0)
  private val jsonSerialiser = new JsonSerialiser
  private val binarySerialize = new BinarySerializer
  private val bitMaskAsJson = """{"bitMask":[24,206158430208]}"""

  "serialize" should {
    "to json" in {
      import BitSetAdapter.writeJsonBitsetAsBitMask
      jsonSerialiser.serialize(dataAsBitSet).toString should equal(bitMaskAsJson)
    }

    "to binary" in {
      val input = BitSet.empty + 3 + 4 + 4 + 100 + 101
      val asBinary = binarySerialize.write(input)
      asBinary should equal(dataAsArrayByte)
    }
  }

  "deserialize" should {
    "from json" in {
      import BitSetAdapter.readJsonBitMaskToBitset
      val deserialized = jsonSerialiser.deserialize(bitMaskAsJson)
      deserialized should equal(dataAsBitSet)
    }

    "to binary" in {
      val result: BitSet = binarySerialize.read(dataAsArrayByte)
      result should equal(dataAsBitSet)
    }
  }
}
