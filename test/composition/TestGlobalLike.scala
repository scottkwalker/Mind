package composition

import com.google.inject.Injector
import filters.WithFilters
import play.api.GlobalSettings

trait TestGlobalLike extends WithFilters with GlobalSettings {

  val injector: Injector

  // Routing leads to this function that instantiates the controller and at the same time injects any dependencies as
  // specified by the IoC.
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)
}