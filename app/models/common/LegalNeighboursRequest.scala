package models.common

import models.common.Scope.Form.scopeId
import play.api.data.Forms.{mapping, number}
import play.api.libs.json.Json

final case class LegalNeighboursRequest(scope: Scope, currentNode: Int)

object LegalNeighboursRequest {

  implicit val jsonFormat = Json.writes[LegalNeighboursRequest]

  object Form {

    final val currentNodeId = "currentNode"

    final val Mapping = mapping(
      scopeId -> Scope.Form.Mapping,
      currentNodeId -> number
    )(LegalNeighboursRequest.apply)(LegalNeighboursRequest.unapply)
  }

}
