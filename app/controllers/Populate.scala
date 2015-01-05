package controllers

import com.google.inject.Inject
import memoization.Generator
import models.common.PopulateRequest
import play.api.data.Form
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class Populate @Inject()(generator: Generator) extends Controller {

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
        generator.generate.map { result =>
          Ok(s"generator returned $result")
        }
      }
    )
  }
}
