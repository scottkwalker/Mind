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

  def present = Action { implicit request =>
    Ok(views.html.legalNeighbours(form))
  }

  def calculate = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        BadRequest(views.html.legalNeighbours(invalidForm)) // TODO display the error messages in the page.
      },
      validForm => {
        // TODO neighbours should be a single id coming in on the form.
        val result = legalNeighboursMemo.fetch(scope = validForm.scope, currentNode = validForm.currentNode)
        Ok(toJson(result))
      }
    )
  }
}
