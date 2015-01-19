package controllers

import composition.TestComposition
import play.api.test.FakeRequest
import play.api.test.Helpers._

final class RoutesSpec extends TestComposition {

  "Routes" must {

    "define GET /" in new App {
      val Some(result) = route(FakeRequest(GET, "/"))
      result must be('defined)
    }

    "define GET /mind/intro" in new App {
      val Some(result) = route(FakeRequest(GET, "/mind/intro"))
      result must be('defined)
    }

    "define GET /mind/health-check" in new App {
      val Some(result) = route(FakeRequest(GET, "/mind/health-check"))
      result must be('defined)
    }

    "define GET /mind/legal-children" in new App {
      val Some(result) = route(FakeRequest(GET, "/mind/legal-children"))
      result must be('defined)
    }

    "define POST /mind/legal-children" in new App {
      val Some(result) = route(FakeRequest(POST, "/mind/legal-children"))
      result must be('defined)
    }

    "define GET /mind/legal-children/size" in new App {
      val Some(result) = route(FakeRequest(GET, "/mind/legal-children/size"))
      result must be('defined)
    }

    "define GET /mind/populate" in new App {
      val Some(result) = route(FakeRequest(GET, "/mind/populate"))
      result must be('defined)
    }

    "define POST /mind/populate" in new App {
      val Some(result) = route(FakeRequest(POST, "/mind/populate"))
      result must be('defined)
    }
  }
}