import modules.DevModule
import com.google.inject.Guice
import play.api.{Application, Play, GlobalSettings}
import play.api.Play.current

object Global extends GlobalSettings {
  // Play.isTest will evaluate to true when you run "play test" from the command line
  // If play is being run to execute the tests then use the TestModule to provide fake
  // implementations of traits otherwise use the DevModule to provide the real ones
  /**
   * Application configuration is in a hierarchy of files:
   *
   * application.conf
   * /             |            \
   * application.prod.conf    application.dev.conf    application.test.conf <- these can override and add to application.conf
   *
   * play test  <- test mode picks up application.test.conf
   * play run   <- dev mode picks up application.dev.conf
   * play start <- prod mode picks up application.prod.conf
   *
   * To override and stipulate a particular "conf" e.g.
   * play -Dconfig.file=conf/application.test.conf run
   */
  def module = if (Play.isTest) new DevModule else new DevModule // TODO we are not using fakes yet.

  lazy val injector = Guice.createInjector(module)


  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

  override def onStart(app: Application): Unit = {
    super.onStart(app)
  }

  override def onStop(app: Application): Unit = {
    super.onStop(app)
  }
}
