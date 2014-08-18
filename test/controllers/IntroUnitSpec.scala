package controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, status}
import utils.helpers.UnitSpec

final class IntroUnitSpec extends UnitSpec {

  "present" should {
    "return http status OK" in {
      val result = intro.present(FakeRequest())
      status(result) should equal(OK)
    }
  }

  val intro = injector.getInstance(classOf[Intro])
}