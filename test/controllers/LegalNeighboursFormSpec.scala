package controllers

import models.common.Scope.Form._
import utils.helpers.UnitSpec

final class LegalNeighboursFormSpec extends UnitSpec {

  "form" should {
    "reject when submission is empty" in {
      val errors = legalNeighbours.form.bind(Map("" -> "")).errors
      errors.length should equal(8)
    }

    "reject when submission contains wrong types" in {
      val errors = formWithValidDefaults(
        numVals = "INVALID",
        numFuncs = "INVALID",
        numObjects = "INVALID",
        height = "INVALID",
        maxExpressionsInFunc = "INVALID",
        maxFuncsInObject = "INVALID",
        maxParamsInFunc = "INVALID",
        maxObjectsInTree = "INVALID"
      ).errors
      errors.length should equal(8)
    }
  }

  private def formWithValidDefaults(numVals: String = "0",
                                    numFuncs: String = "0",
                                    numObjects: String = "0",
                                    height: String = "0",
                                    maxExpressionsInFunc: String = "0",
                                    maxFuncsInObject: String = "0",
                                    maxParamsInFunc: String = "0",
                                    maxObjectsInTree: String = "0") = {
    legalNeighbours.form.bind(
      Map(
        numValsId -> numVals,
        numFuncsId -> numFuncs,
        numObjectsId -> numObjects,
        heightId -> height,
        maxExpressionsInFuncId -> maxExpressionsInFunc,
        maxFuncsInObjectId -> maxFuncsInObject,
        maxParamsInFuncId -> maxParamsInFunc,
        maxObjectsInTreeId -> maxObjectsInTree
      )
    )
  }

  private val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
}
