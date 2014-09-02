package models.common

import models.common.Scope.Form.ScopeId
import play.api.data.Forms.{mapping, number}
import play.api.libs.json.Json

final case class LegalNeighboursRequest(scope: Scope, currentNode: Int)

object LegalNeighboursRequest {

  implicit val jsonFormat = Json.writes[LegalNeighboursRequest]

  object Form {

    final val CurrentNodeId = "currentNode"
    final val CurrentNodeMaxLength = 2

    final val Mapping = mapping(
      ScopeId -> Scope.Form.Mapping,
      CurrentNodeId -> number
    )(LegalNeighboursRequest.apply)(LegalNeighboursRequest.unapply)
  }

}
