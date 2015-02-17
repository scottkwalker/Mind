package controllers

import composition.TestHelpers
import composition.TestComposition
import play.api.test.FakeRequest
import play.api.test.Helpers.OK
import play.api.test.Helpers.contentAsString
import play.api.test.Helpers.status

final class IntroUnitSpec extends TestHelpers with TestComposition {

  "present" must {
    "return http status OK" in {
      val result = intro.present(FakeRequest())
      status(result) must equal(OK)
    }

    "contain expected text" in {
      val result = intro.present(FakeRequest())
      contentAsString(result) must include("Mind")
    }
  }

  private def intro = testInjector().getInstance(classOf[Intro])
}