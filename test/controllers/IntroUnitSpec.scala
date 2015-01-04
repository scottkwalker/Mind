package controllers

import composition.TestComposition
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, status}

final class IntroUnitSpec extends TestComposition {

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