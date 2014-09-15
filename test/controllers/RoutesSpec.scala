package controllers

import composition.TestComposition
import play.api.test.FakeRequest
import play.api.test.Helpers._

final class RoutesSpec extends TestComposition {

  "Routes" must {
    "define GET /mind/health-check" in new App {
      val Some(result) = route(FakeRequest(GET, "/mind/health-check"))
      result must be('defined)
    }
  }
}