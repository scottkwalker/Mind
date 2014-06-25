package models.domain.common

import play.api.data.validation.ValidationError
import play.api.libs.json.JsPath

final case class JsonValidationException(errors: Seq[(JsPath, Seq[ValidationError])]) extends Exception