package composition

import com.google.inject.Injector
import filters.WithFilters
import play.api.GlobalSettings

class InjectableGlobal(testInjector: Injector) extends WithFilters with GlobalSettings with Composition {

  // Routing leads to this function that instantiates the controller and at the same time injects any dependencies as
  // specified by the IoC.
  override def getControllerInstance[A](controllerClass: Class[A]): A = testInjector.getInstance(controllerClass)
}