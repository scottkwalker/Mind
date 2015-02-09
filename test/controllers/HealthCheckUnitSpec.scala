package controllers

import composition.TestComposition
import play.api.test.FakeRequest
import play.api.test.Helpers.OK
import play.api.test.Helpers.charset
import play.api.test.Helpers.contentType
import play.api.test.Helpers.status

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
  }

  private def healthCheckResponse = {
    val healthCheck = testInjector().getInstance(classOf[HealthCheck])
    healthCheck.respond(FakeRequest())
  }
}