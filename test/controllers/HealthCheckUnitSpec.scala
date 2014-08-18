package controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, status}
import utils.helpers.UnitSpec

final class HealthCheckUnitSpec extends UnitSpec {

  "respond" should {
    "return http status OK" in {
      val result = healthCheck.respond(FakeRequest())
      status(result) should equal(OK)
    }

    "contain expected text" in {
      val result = healthCheck.respond(FakeRequest())
      contentAsString(result) should include("Health check")
    }
  }

  val healthCheck = injector.getInstance(classOf[HealthCheck])
}