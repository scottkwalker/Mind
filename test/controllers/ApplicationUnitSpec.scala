package controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.LOCATION
import utils.helpers.UnitSpec2

final class ApplicationUnitSpec extends UnitSpec2 {

  "present" must {
    "redirect to Intro page" in {
      val result = application.index(FakeRequest())
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) must equal(Some(routes.Intro.present().url))
      }
    }
  }

  val application = injector.getInstance(classOf[Application])
}