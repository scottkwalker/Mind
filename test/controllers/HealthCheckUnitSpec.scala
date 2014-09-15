package controllers

import composition.TestComposition
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, charset, contentAsString, contentType, status}

final class HealthCheckUnitSpec extends TestComposition {

  "respond" must {
    "return http status OK" in {
      status(healthCheckResponse) must equal(OK)
    }

    "return html content type" in {
      contentType(healthCheckResponse) mustEqual Some("text/html")
    }

    "return UTF-8 encoding" in {
      charset(healthCheckResponse) mustEqual Some("utf-8")
    }

    "contain expected text" in {
      contentAsString(healthCheckResponse) must include("Health check")
    }
  }

  val healthCheckResponse = {
    val healthCheck = injector.getInstance(classOf[HealthCheck])
    healthCheck.respond(FakeRequest())
  }
}