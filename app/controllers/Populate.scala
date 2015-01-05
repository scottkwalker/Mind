package controllers

import com.google.inject.Inject
import memoization.LookupChildren
import models.common.PopulateRequest
import play.api.data.Form
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class Populate @Inject()(lookupChildren: LookupChildren) extends Controller {

  private[controllers] val form = Form(
    PopulateRequest.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.populate(form))
  }

  def calculate = Action.async { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        Future.successful(BadRequest(views.html.populate(invalidForm)))
      },
      validForm => {
        lookupChildren.fetch(scope = validForm.scope, parent = PozInt(validForm.currentNode)).map { result =>
          Ok(toJson(result.map{_.value})).as(JSON)
        }
      }
    )
  }
}
