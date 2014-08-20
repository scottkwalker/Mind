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

      errors(0).key should equal(s"$scopeId.$numValsId")
      errors(1).key should equal(s"$scopeId.$numFuncsId")
      errors(2).key should equal(s"$scopeId.$numObjectsId")
      errors(3).key should equal(s"$scopeId.$heightId")
      errors(4).key should equal(s"$scopeId.$maxExpressionsInFuncId")
      errors(5).key should equal(s"$scopeId.$maxFuncsInObjectId")
      errors(6).key should equal(s"$scopeId.$maxParamsInFuncId")
      errors(7).key should equal(s"$scopeId.$maxObjectsInTreeId")

      for (i <- 0 until errors.length) {
        errors(i).messages should equal(List("error.number"))
      }
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
      model.scope.numVals should equal(numVals)
      model.scope.numFuncs should equal(numFuncs)
      model.scope.numObjects should equal(numObjects)
      model.scope.height should equal(height)
      model.scope.maxExpressionsInFunc should equal(maxExpressionsInFunc)
      model.scope.maxFuncsInObject should equal(maxFuncsInObject)
      model.scope.maxParamsInFunc should equal(maxParamsInFunc)
      model.scope.maxObjectsInTree should equal(maxObjectsInTree)
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
        s"$scopeId.$numValsId" -> numVals,
        s"$scopeId.$numFuncsId" -> numFuncs,
        s"$scopeId.$numObjectsId" -> numObjects,
        s"$scopeId.$heightId" -> height,
        s"$scopeId.$maxExpressionsInFuncId" -> maxExpressionsInFunc,
        s"$scopeId.$maxFuncsInObjectId" -> maxFuncsInObject,
        s"$scopeId.$maxParamsInFuncId" -> maxParamsInFunc,
        s"$scopeId.$maxObjectsInTreeId" -> maxObjectsInTree
      )
    )
  }

  private val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
}
