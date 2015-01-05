package controllers

import composition.TestComposition
import models.common.Scope.Form._
import models.common.PopulateRequest.Form.MaxScopeId

final class PopulateFormSpec extends TestComposition {

  "form" must {
    "reject when submission is empty" in {
      val errors = populate.form.bind(Map("" -> "")).errors
      errors.length must equal(8)
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
        maxObjectsInTree = "INVALID",
        currentNode = "INVALID"
      ).errors

      errors.length must equal(8)

      errors(0).key must equal(s"$MaxScopeId.$NumValsId")
      errors(1).key must equal(s"$MaxScopeId.$NumFuncsId")
      errors(2).key must equal(s"$MaxScopeId.$NumObjectsId")
      errors(3).key must equal(s"$MaxScopeId.$HeightId")
      errors(4).key must equal(s"$MaxScopeId.$MaxExpressionsInFuncId")
      errors(5).key must equal(s"$MaxScopeId.$MaxFuncsInObjectId")
      errors(6).key must equal(s"$MaxScopeId.$MaxParamsInFuncId")
      errors(7).key must equal(s"$MaxScopeId.$MaxObjectsInTreeId")

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.number"))
      }
    }

    "reject when input is above max" in {
      val errors = formWithValidDefaults(
        numVals = "100",
        numFuncs = "100",
        numObjects = "100",
        height = "100",
        maxExpressionsInFunc = "100",
        maxFuncsInObject = "100",
        maxParamsInFunc = "100",
        maxObjectsInTree = "100",
        currentNode = "100"
      ).errors

      errors.length must equal(8)

      errors(0).key must equal(s"$MaxScopeId.$NumValsId")
      errors(1).key must equal(s"$MaxScopeId.$NumFuncsId")
      errors(2).key must equal(s"$MaxScopeId.$NumObjectsId")
      errors(3).key must equal(s"$MaxScopeId.$HeightId")
      errors(4).key must equal(s"$MaxScopeId.$MaxExpressionsInFuncId")
      errors(5).key must equal(s"$MaxScopeId.$MaxFuncsInObjectId")
      errors(6).key must equal(s"$MaxScopeId.$MaxParamsInFuncId")
      errors(7).key must equal(s"$MaxScopeId.$MaxObjectsInTreeId")

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.max"))
      }
    }

    "reject when input is below min" in {
      val errors = formWithValidDefaults(
        numVals = "-1",
        numFuncs = "-1",
        numObjects = "-1",
        height = "-1",
        maxExpressionsInFunc = "-1",
        maxFuncsInObject = "-1",
        maxParamsInFunc = "-1",
        maxObjectsInTree = "-1",
        currentNode = "-1"
      ).errors

      errors.length must equal(8)

      errors(0).key must equal(s"$MaxScopeId.$NumValsId")
      errors(1).key must equal(s"$MaxScopeId.$NumFuncsId")
      errors(2).key must equal(s"$MaxScopeId.$NumObjectsId")
      errors(3).key must equal(s"$MaxScopeId.$HeightId")
      errors(4).key must equal(s"$MaxScopeId.$MaxExpressionsInFuncId")
      errors(5).key must equal(s"$MaxScopeId.$MaxFuncsInObjectId")
      errors(6).key must equal(s"$MaxScopeId.$MaxParamsInFuncId")
      errors(7).key must equal(s"$MaxScopeId.$MaxObjectsInTreeId")

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.min"))
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
      val currentNode = 1
      val result = formWithValidDefaults(
        numVals.toString,
        numFuncs.toString,
        numObjects.toString,
        height.toString,
        maxExpressionsInFunc.toString,
        maxFuncsInObject.toString,
        maxParamsInFunc.toString,
        maxObjectsInTree.toString,
        currentNode.toString
      )
      result.errors.length must equal(0)
      val model = result.get
      model.maxScope.numVals must equal(numVals)
      model.maxScope.numFuncs must equal(numFuncs)
      model.maxScope.numObjects must equal(numObjects)
      model.maxScope.height must equal(height)
      model.maxScope.maxExpressionsInFunc must equal(maxExpressionsInFunc)
      model.maxScope.maxFuncsInObject must equal(maxFuncsInObject)
      model.maxScope.maxParamsInFunc must equal(maxParamsInFunc)
      model.maxScope.maxObjectsInTree must equal(maxObjectsInTree)
    }
  }

  private def populate = testInjector().getInstance(classOf[Populate])

  private def formWithValidDefaults(numVals: String,
                                    numFuncs: String,
                                    numObjects: String,
                                    height: String,
                                    maxExpressionsInFunc: String,
                                    maxFuncsInObject: String,
                                    maxParamsInFunc: String,
                                    maxObjectsInTree: String,
                                    currentNode: String) = {
    populate.form.bind(
      Map(
        s"$MaxScopeId.$NumValsId" -> numVals,
        s"$MaxScopeId.$NumFuncsId" -> numFuncs,
        s"$MaxScopeId.$NumObjectsId" -> numObjects,
        s"$MaxScopeId.$HeightId" -> height,
        s"$MaxScopeId.$MaxExpressionsInFuncId" -> maxExpressionsInFunc,
        s"$MaxScopeId.$MaxFuncsInObjectId" -> maxFuncsInObject,
        s"$MaxScopeId.$MaxParamsInFuncId" -> maxParamsInFunc,
        s"$MaxScopeId.$MaxObjectsInTreeId" -> maxObjectsInTree
      )
    )
  }
}
