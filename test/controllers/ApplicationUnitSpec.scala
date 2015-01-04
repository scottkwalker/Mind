package controllers

import composition.TestComposition
import play.api.test.Helpers.LOCATION
import play.api.test.{FakeRequest, WithApplication}

final class ApplicationUnitSpec extends TestComposition {

  "present" must {
    "redirect to Intro page" in new WithApplication {
      val result = application.index(FakeRequest())
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) must equal(Some(routes.Intro.present().url))
      }
    }
  }

  private def application = testInjector().getInstance(classOf[Application])
}