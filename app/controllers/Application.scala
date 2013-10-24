package controllers


object Application extends Controller {

  def index = Action {
    Ok(<p>Hello, World!</p>)
  }

}