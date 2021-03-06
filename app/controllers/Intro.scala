package controllers

import com.google.inject.Inject
import play.api.mvc.Action
import play.api.mvc.Controller

final class Intro @Inject()() extends Controller {

  def present = Action { implicit request =>
    Ok(views.html.intro())
  }
}
