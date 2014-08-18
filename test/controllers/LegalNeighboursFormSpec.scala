package controllers

import models.common.Scope.Form._
import utils.helpers.UnitSpec

final class LegalNeighboursFormSpec extends UnitSpec {

  "form" should {
    "reject when submission is empty" in {
      val errors = legalNeighbours.form.bind(Map("" -> "")).errors
      errors.length should equal(8)
    }
  }


  private val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
}
