package controllers

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.LegalNeighboursRequest
import play.api.data.Form
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class LegalNeighbours @Inject()(lookupNeighbours: LookupNeighbours) extends Controller {

  private[controllers] val form = Form(
    LegalNeighboursRequest.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.legalNeighbours(form))
  }

  def calculate = Action.async { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        Future.successful(BadRequest(views.html.legalNeighbours(invalidForm)))
      },
      validForm => {
        lookupNeighbours.fetch(scope = validForm.scope, currentNode = validForm.currentNode).map { result =>
          Ok(toJson(result)).as(JSON)
        }
      }
    )
  }
}
