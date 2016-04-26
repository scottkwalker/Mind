package serialization

import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.json.Json.parse

object JsonDeserialiser {

  def deserialize[A : Reads](data: String): A = {
    deserialize(parse(data))
  }

  def deserialize[A : Reads](data: JsValue): A = {
    val fromJson = Json.fromJson[A](data)
    fromJson match {
      case JsSuccess(model, _) => model
      case JsError(errors) => throw JsonValidationException(errors)
    }
  }
}
