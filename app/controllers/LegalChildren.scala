package controllers

import com.google.inject.Inject
import memoization.LookupChildren
import models.common.LookupChildrenRequest
import play.api.data.Form
import play.api.libs.json.Json.toJson
import play.api.mvc.Action
import play.api.mvc.Controller
import utils.PozInt

final class LegalChildren @Inject()(lookupChildren: LookupChildren)
    extends Controller {

  private[controllers] val form = Form(
      LookupChildrenRequest.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.legalChildren(form))
  }

  def calculate = Action { implicit request =>
    form.bindFromRequest.fold(
        invalidForm =>
          {
            BadRequest(views.html.legalChildren(invalidForm))
        },
        validForm =>
          {
            val scope = validForm.scope
            val parent = PozInt(validForm.currentNode)
            val children = lookupChildren.get(scope = scope, parent = parent)
            val childrenJson = toJson(children.map(_.value))
            Ok(childrenJson).as(JSON)
        }
    )
  }

  def size = Action {
    Ok(s"repository size: ${lookupChildren.size}")
  }
}
