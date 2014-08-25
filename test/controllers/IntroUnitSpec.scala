package controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, status}
import utils.helpers.UnitSpec2

final class IntroUnitSpec extends UnitSpec2 {

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

  val intro = injector.getInstance(classOf[Intro])
}