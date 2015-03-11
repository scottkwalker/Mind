package controllers

import composition.TestComposition
import composition.UnitTestHelpers
import play.api.test.FakeRequest
import play.api.test.Helpers.LOCATION

final class ApplicationUnitSpec extends UnitTestHelpers with TestComposition {

  "present" must {
    "redirect to Intro page" in {
      val present = application.index(FakeRequest())
      whenReady(present) { r =>
        r.header.headers.get(LOCATION) must equal(Some(routes.Intro.present().url))
      }(config = patienceConfig)
    }
  }

  private def application = testInjector().getInstance(classOf[Application])
}