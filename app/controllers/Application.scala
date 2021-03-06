package controllers

import com.google.inject.Inject
import play.api.mvc.Action
import play.api.mvc.Controller

final class Application @Inject()() extends Controller {

  def index = Action {
    Redirect(routes.Intro.present)
  }
}
