package controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, status}
import utils.helpers.UnitSpec

final class IntroUnitSpec extends UnitSpec {

  "present" should {
    "return http status OK" in {
      val result = intro.present(FakeRequest())
      status(result) should equal(OK)
    }

    "contain expected text" in {
      val result = intro.present(FakeRequest())
      contentAsString(result) should include("Mind")
    }
  }

  val intro = injector.getInstance(classOf[Intro])
}