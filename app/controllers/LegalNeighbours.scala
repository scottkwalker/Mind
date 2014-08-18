package controllers

import com.google.inject.Inject
import models.common.Scope
import play.api.data.Form
import play.api.mvc.{Action, Controller}

final class LegalNeighbours @Inject()() extends Controller {

  private[controllers] val form = Form(
    Scope.Form.Mapping
  )

  def calculate = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => BadRequest,
      validForm => Ok("<p>Hello, world!</p>")
    )
  }
}
