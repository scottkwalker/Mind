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

    "accept when submission is valid" in {
      val numVals = 1
      val numFuncs = 2
      val numObjects = 3
      val height = 4
      val maxExpressionsInFunc = 5
      val maxFuncsInObject = 6
      val maxParamsInFunc = 7
      val maxObjectsInTree = 8
      val result = formWithValidDefaults(
        numVals.toString,
        numFuncs.toString,
        numObjects.toString,
        height.toString,
        maxExpressionsInFunc.toString,
        maxFuncsInObject.toString,
        maxParamsInFunc.toString,
        maxObjectsInTree.toString
      )
      result.errors.length should equal(0)
      val model = result.get
      model.numVals should equal(numVals)
      model.numFuncs should equal(numFuncs)
      model.numObjects should equal(numObjects)
      model.height should equal(height)
      model.maxExpressionsInFunc should equal(maxExpressionsInFunc)
      model.maxFuncsInObject should equal(maxFuncsInObject)
      model.maxParamsInFunc should equal(maxParamsInFunc)
      model.maxObjectsInTree should equal(maxObjectsInTree)
    }
  }

  private def formWithValidDefaults(numVals: String,
                                    numFuncs: String,
                                    numObjects: String,
                                    height: String,
                                    maxExpressionsInFunc: String,
                                    maxFuncsInObject: String,
                                    maxParamsInFunc: String,
                                    maxObjectsInTree: String) = {
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
