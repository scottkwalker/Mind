package composition

import com.google.inject.Injector
import play.api.test.FakeApplication

trait ApplicationWithTestGlobal extends FakeApplication {

  // A FakeApplication with the global stubbed to use the test version of the IoC injector - all services are stubbed
  // so that we can test the UI in isolation from the backend.
  //override val mode : play.api.Mode.Value // Dev, Test, Prod
  //override def configuration : play.api.Configuration
  override lazy val global: play.api.GlobalSettings = TestGlobal
  val injector: Injector
}