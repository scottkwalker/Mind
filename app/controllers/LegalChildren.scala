package controllers

import com.google.inject.Inject
import memoization.LookupChildren
import models.common.LookupChildrenRequest
import play.api.data.Form
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class LegalChildren @Inject()(lookupChildren: LookupChildren) extends Controller {

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
        lookupChildren.fetch(scope = validForm.scope, currentNode = validForm.currentNode).map { result =>
          Ok(toJson(result)).as(JSON)
        }
      }
    )
  }
}
