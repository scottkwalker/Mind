package controllers

import composition.TestComposition
import composition.UnitTestHelpers
import play.api.test.FakeRequest
import play.api.test.Helpers.OK
import play.api.test.Helpers.contentAsString
import play.api.test.Helpers.status

final class IntroUnitSpec extends UnitTestHelpers with TestComposition {

  "present" must {
    "return http status OK" in {
      val present = intro.present(FakeRequest())
      status(present) must equal(OK)
    }

    "contain expected text" in {
      val present = intro.present(FakeRequest())
      contentAsString(present) must include("Mind")
    }
  }

  private def intro = testInjector().getInstance(classOf[Intro])
}