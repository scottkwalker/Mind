package models.common

import play.api.data.Forms.mapping
import play.api.data.Forms.number
import play.api.libs.json.Json

final case class LookupChildrenRequest(scope: Scope, currentNode: Int)

object LookupChildrenRequest {

  implicit val jsonFormat = Json.writes[LookupChildrenRequest]

  object Form {

    val ScopeId = "scope"
    val CurrentNodeId = "currentNode"
    val CurrentNodeMin = 0
    val CurrentNodeMax = 99
    val CurrentNodeMinLength = 1
    val CurrentNodeMaxLength = 2

    val Mapping = mapping(
      ScopeId -> Scope.Form.MappingWithCurrentAndMax,
      CurrentNodeId -> number(min = CurrentNodeMin, max = CurrentNodeMax)
    )(LookupChildrenRequest.apply)(LookupChildrenRequest.unapply)
  }

}
