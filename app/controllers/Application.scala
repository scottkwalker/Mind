package controllers

import play.api.mvc._
import com.google.inject.{Guice, Injector}

object Application extends Controller {

  def index = Action {
    Ok(<p>Hello, World!
    </p>
    )
  }
}