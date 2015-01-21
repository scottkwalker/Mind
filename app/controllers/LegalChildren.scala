package controllers

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.LookupChildrenRequest
import play.api.data.Form
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class LegalChildren @Inject()(lookupChildren: LookupChildrenWithFutures) extends Controller {

  private[controllers] val form = Form(
    LookupChildrenRequest.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.legalChildren(form))
  }

  def calculate = Action.async { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        Future.successful(BadRequest(views.html.legalChildren(invalidForm)))
      },
      validForm => {
        lookupChildren.fetch(scope = validForm.scope, parent = PozInt(validForm.currentNode)).map { result =>
          Ok(toJson(result.map {
            _.value
          })).as(JSON)
        }
      }
    )
  }

  def size = Action { implicit request =>
    Ok(s"repository size: ${lookupChildren.size}")
  }

  def sizeOfCalculated = Action { implicit request =>
    Ok(s"size-of-calculated: ${lookupChildren.sizeOfCalculated}")
  }
}
