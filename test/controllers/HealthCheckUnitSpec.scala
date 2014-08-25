package controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, status}
import utils.helpers.UnitSpec2

final class HealthCheckUnitSpec extends UnitSpec2 {

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