package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(<p>Hello, World!
    </p>
    )
  }
}