package controllers

import com.google.inject.Inject
import models.common.Scope
import nodes.NodeTreeFactoryImpl
import play.api.data.Form
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

final class LegalNeighbours @Inject()() extends Controller {

  private[controllers] val form = Form(
    Scope.Form.Mapping
  )

  def calculate = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        BadRequest(s"form errors: ${invalidForm.errors}")
      },
      validForm => {
        val result = if (validForm.hasHeightRemaining) Seq(NodeTreeFactoryImpl.id) else Seq.empty
        Ok(toJson(result))
      }
    )
  }
}
