package serialization

import play.api.libs.json.Json.parse
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads

object JsonDeserialiser {

  def deserialize[A: Reads](data: String): A = {
    deserialize(parse(data))
  }

  def deserialize[A: Reads](data: JsValue): A = {
    val fromJson = Json.fromJson[A](data)
    fromJson.asEither match {
      case Left(errors) => throw JsonValidationException(errors)
      case Right(model) => model
    }
  }
}
