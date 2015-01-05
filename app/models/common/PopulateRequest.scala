package models.common

import models.common.Scope.Form.ScopeId
import play.api.data.Forms.{mapping, number}
import play.api.libs.json.Json

final case class PopulateRequest(scope: Scope, currentNode: Int)

object PopulateRequest {

  implicit val jsonFormat = Json.writes[PopulateRequest]

  object Form {

    val CurrentNodeId = "currentNode"
    val CurrentNodeMin = 0
    val CurrentNodeMax = 99
    val CurrentNodeMinLength = 1
    val CurrentNodeMaxLength = 2

    val Mapping = mapping(
      ScopeId -> Scope.Form.Mapping,
      CurrentNodeId -> number(min = CurrentNodeMin, max = CurrentNodeMax)
    )(PopulateRequest.apply)(PopulateRequest.unapply)
  }

}
