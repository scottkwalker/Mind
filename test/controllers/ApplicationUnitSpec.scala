package controllers

import composition.TestHelpers
import composition.TestComposition
import play.api.test.FakeRequest
import play.api.test.Helpers.LOCATION

final class ApplicationUnitSpec extends TestHelpers with TestComposition {

  "present" must {
    "redirect to Intro page" in {
      val result = application.index(FakeRequest())
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) must equal(Some(routes.Intro.present().url))
      }(config = patienceConfig)
    }
  }

  private def application = testInjector().getInstance(classOf[Application])
}