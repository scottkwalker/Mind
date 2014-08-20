package models.common

import models.common.Scope.Form.scopeId
import play.api.data.Forms.mapping
import play.api.libs.json.Json

final case class LegalNeighboursRequest(scope: Scope)

object LegalNeighboursRequest {

  implicit val jsonFormat = Json.writes[LegalNeighboursRequest]

  object Form {

    final val Mapping = mapping(
      scopeId -> Scope.Form.Mapping
    )(LegalNeighboursRequest.apply)(LegalNeighboursRequest.unapply)
  }

}
