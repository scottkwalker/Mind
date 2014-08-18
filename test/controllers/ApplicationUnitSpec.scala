package controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.LOCATION
import utils.helpers.UnitSpec

final class ApplicationUnitSpec extends UnitSpec {

  "present" should {
    "redirect to Intro page" in {
      val result = application.index(FakeRequest())
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(routes.Intro.present().url))
      }
    }
  }

  val application = injector.getInstance(classOf[Application])
}