package nodes.helpers

import models.common.JsonValidationException
import play.api.libs.json.Json.parse
import play.api.libs.json.{JsValue, Json, Reads}

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
