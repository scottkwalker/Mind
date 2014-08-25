package controllers

import composition.TestComposition
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, status}

final class HealthCheckUnitSpec extends TestComposition {

  "respond" must {
    "return http status OK" in {
      val result = healthCheck.respond(FakeRequest())
      status(result) must equal(OK)
    }

    "contain expected text" in {
      val result = healthCheck.respond(FakeRequest())
      contentAsString(result) must include("Health check")
    }
  }

  val healthCheck = injector.getInstance(classOf[HealthCheck])
}