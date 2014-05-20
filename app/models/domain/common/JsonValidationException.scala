package models.domain.common

import play.api.libs.json.JsPath
import play.api.data.validation.ValidationError

final case class JsonValidationException(errors: Seq[(JsPath, Seq[ValidationError])]) extends Exception