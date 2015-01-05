package models.common

import play.api.data.Forms.mapping
import play.api.libs.json.Json

final case class PopulateRequest(maxScope: Scope)

object PopulateRequest {

  implicit val jsonFormat = Json.writes[PopulateRequest]

  object Form {

    val MaxScopeId = "maxScope"

    val Mapping = mapping(
      MaxScopeId -> Scope.Form.Mapping
    )(PopulateRequest.apply)(PopulateRequest.unapply)
  }

}
