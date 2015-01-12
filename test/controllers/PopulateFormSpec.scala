package controllers

import composition.TestComposition
import models.common.Scope.Form._
import models.common.PopulateRequest.Form.MaxScopeId

final class PopulateFormSpec extends TestComposition {

  "form" must {
    "reject when submission is empty" in {
      val errors = populate.form.bind(Map("" -> "")).errors
      errors.length must equal(4)
    }

    "reject when submission contains wrong types" in {
      val errors = formWithValidDefaults(
        height = "INVALID",
        maxFuncsInObject = "INVALID",
        maxParamsInFunc = "INVALID",
        maxObjectsInTree = "INVALID",
        currentNode = "INVALID"
      ).errors

      errors.length must equal(4)

      errors(0).key must equal(s"$MaxScopeId.$HeightId")
      errors(1).key must equal(s"$MaxScopeId.$MaxFuncsInObjectId")
      errors(2).key must equal(s"$MaxScopeId.$MaxParamsInFuncId")
      errors(3).key must equal(s"$MaxScopeId.$MaxObjectsInTreeId")

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.number"))
      }
    }

    "reject when input is above max" in {
      val errors = formWithValidDefaults(
        height = "100",
        maxFuncsInObject = "100",
        maxParamsInFunc = "100",
        maxObjectsInTree = "100",
        currentNode = "100"
      ).errors

      errors.length must equal(4)

      errors(0).key must equal(s"$MaxScopeId.$HeightId")
      errors(1).key must equal(s"$MaxScopeId.$MaxFuncsInObjectId")
      errors(2).key must equal(s"$MaxScopeId.$MaxParamsInFuncId")
      errors(3).key must equal(s"$MaxScopeId.$MaxObjectsInTreeId")

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.max"))
      }
    }

    "reject when input is below min" in {
      val errors = formWithValidDefaults(
        height = "-1",
        maxFuncsInObject = "-1",
        maxParamsInFunc = "-1",
        maxObjectsInTree = "-1",
        currentNode = "-1"
      ).errors

      errors.length must equal(4)

      errors(0).key must equal(s"$MaxScopeId.$HeightId")
      errors(1).key must equal(s"$MaxScopeId.$MaxFuncsInObjectId")
      errors(2).key must equal(s"$MaxScopeId.$MaxParamsInFuncId")
      errors(3).key must equal(s"$MaxScopeId.$MaxObjectsInTreeId")

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.min"))
      }
    }

    "ignored values will be set to zero when submission is valid but values are provided" in {
      val numVals = 42
      val numFuncs = 42
      val numObjects = 42
      val height = 4
      val maxExpressionsInFunc = 42
      val maxFuncsInObject = 6
      val maxParamsInFunc = 7
      val maxObjectsInTree = 8
      val currentNode = 1
      val result = formWithIgnoredFields(
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
      model.maxScope.numVals must equal(0)
      model.maxScope.numFuncs must equal(0)
      model.maxScope.numObjects must equal(0)
      model.maxScope.height must equal(height)
      model.maxScope.maxExpressionsInFunc must equal(0)
      model.maxScope.maxFuncsInObject must equal(maxFuncsInObject)
      model.maxScope.maxParamsInFunc must equal(maxParamsInFunc)
      model.maxScope.maxObjectsInTree must equal(maxObjectsInTree)
    }

    "accept when submission is valid" in {
      val height = 4
      val maxFuncsInObject = 6
      val maxParamsInFunc = 7
      val maxObjectsInTree = 8
      val currentNode = 1
      val result = formWithValidDefaults(
        height.toString,
        maxFuncsInObject.toString,
        maxParamsInFunc.toString,
        maxObjectsInTree.toString,
        currentNode.toString
      )
      result.errors.length must equal(0)
      val model = result.get
      model.maxScope.numVals must equal(0)
      model.maxScope.numFuncs must equal(0)
      model.maxScope.numObjects must equal(0)
      model.maxScope.height must equal(height)
      model.maxScope.maxExpressionsInFunc must equal(0)
      model.maxScope.maxFuncsInObject must equal(maxFuncsInObject)
      model.maxScope.maxParamsInFunc must equal(maxParamsInFunc)
      model.maxScope.maxObjectsInTree must equal(maxObjectsInTree)
    }
  }

  private def populate = testInjector().getInstance(classOf[Populate])

  private def formWithValidDefaults(
                                    height: String,
                                    maxFuncsInObject: String,
                                    maxParamsInFunc: String,
                                    maxObjectsInTree: String,
                                    currentNode: String) = {
    populate.form.bind(
      Map(
        s"$MaxScopeId.$HeightId" -> height,
        s"$MaxScopeId.$MaxFuncsInObjectId" -> maxFuncsInObject,
        s"$MaxScopeId.$MaxParamsInFuncId" -> maxParamsInFunc,
        s"$MaxScopeId.$MaxObjectsInTreeId" -> maxObjectsInTree
      )
    )
  }

  private def formWithIgnoredFields(numVals: String,
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
