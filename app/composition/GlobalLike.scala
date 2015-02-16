package composition

import java.io.File

import com.google.inject.Injector
import com.typesafe.config.ConfigFactory
import filters.WithFilters
import play.api.Application
import play.api.Configuration
import play.api.GlobalSettings
import play.api.Mode

trait GlobalLike extends WithFilters with GlobalSettings {

  // Use mixin to define the values in the injector that will be used for IoC. These can be either test or production
  // modules.
  val injector: Injector

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
  override def onLoadConfig(configuration: Configuration,
                            path: File,
                            classloader: ClassLoader,
                            mode: Mode.Mode): Configuration = {
    val applicationConf = System.getProperty("config.file", s"application.${mode.toString.toLowerCase}.conf")
    val environmentOverridingConfiguration = configuration ++
      Configuration(ConfigFactory.load(applicationConf))
    super.onLoadConfig(environmentOverridingConfiguration, path, classloader, mode)
  }

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
