package nodes.helpers

import play.api.libs.json.{Reads, Writes, Json}
import models.domain.common.JsonValidationException

final class JsonSerialiser {
  def deserialize[A: Reads](data: String) = {
    val parsed = Json.parse(data)
    val fromJson = Json.fromJson[A](parsed)
    fromJson.asEither match {
      case Left(errors) => throw JsonValidationException(errors)
      case Right(model) => model
    }
  }
}
