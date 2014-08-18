package controllers

import play.api.test.Helpers.BAD_REQUEST
import play.api.test.{FakeRequest, WithApplication}
import utils.helpers.UnitSpec

final class LegalNeighboursUnitSpec extends UnitSpec {

  "calculate" should {
    "return bad request when submission is empty" in new WithApplication {
      val request = FakeRequest().withFormUrlEncodedBody()
      val result = legalNeighbours.calculate(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }
  }

  private val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
}
