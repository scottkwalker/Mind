package controllers

import com.google.inject.Inject
import memoization.LegalNeighboursMemo
import models.common.LegalNeighboursRequest
import play.api.data.Form
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

final class LegalNeighbours @Inject()(legalNeighboursMemo: LegalNeighboursMemo) extends Controller {

  private[controllers] val form = Form(
    LegalNeighboursRequest.Form.Mapping
  )

  def calculate = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        BadRequest(s"form errors: ${invalidForm.errors}")
      },
      validForm => {
        // TODO neighbours should be a single id coming in on the form.
        val result = legalNeighboursMemo.fetch(scope = validForm.scope, currentNode = validForm.currentNode)
        Ok(toJson(result))
      }
    )
  }
}
