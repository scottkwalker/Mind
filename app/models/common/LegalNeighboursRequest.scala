package models.common

import models.common.Scope.Form.ScopeId
import play.api.data.Forms.{mapping, number}
import play.api.libs.json.Json

final case class LegalNeighboursRequest(scope: Scope, currentNode: Int)

object LegalNeighboursRequest {

  implicit val jsonFormat = Json.writes[LegalNeighboursRequest]

  object Form {

    val CurrentNodeId = "currentNode"
    val CurrentNodeMin = 0
    val CurrentNodeMax = 99
    val CurrentNodeMaxLength = 2

    val Mapping = mapping(
      ScopeId -> Scope.Form.Mapping,
      CurrentNodeId -> number(min = CurrentNodeMin, max = CurrentNodeMax)
    )(LegalNeighboursRequest.apply)(LegalNeighboursRequest.unapply)
  }

}
