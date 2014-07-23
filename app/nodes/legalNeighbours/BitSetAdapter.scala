package nodes.legalNeighbours

import play.api.libs.json._
import scala.collection.immutable.BitSet

object BitSetAdapter {

  implicit val writeJsonBitsetAsBitMask = new Writes[BitSet] {
    def writes(data: BitSet): JsValue = Json.obj("bitMask" -> data.toBitMask)
  }

  implicit val readJsonBitMaskToBitset: Reads[BitSet] = (__ \ "bitMask").read[Array[Long]].map(BitSet.fromBitMask)
}
