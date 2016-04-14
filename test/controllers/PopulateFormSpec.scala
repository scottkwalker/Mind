package controllers

import composition.StubGeneratorBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.PopulateRequest.Form.MaxScopeId
import models.common.Scope.Form._

final class PopulateFormSpec extends UnitTestHelpers with TestComposition {

  "form" must {
    "reject if given an empty submission" in {
      val errors = populate.form.bind(Map("" -> "")).errors
      errors.length must equal(5)
    }

    "reject if given a submission containing wrong types e.g. strings instead of integers" in {
      val errors = formBuilder(
        height = "INVALID",
        maxFuncsInObject = "INVALID",
        maxParamsInFunc = "INVALID",
        maxObjectsInTree = "INVALID",
        maxHeight = "INVALID",
        currentNode = "INVALID"
      ).errors

      errors.length must equal(5)

      errors(0).key must equal(s"$MaxScopeId.$HeightId")
      errors(1).key must equal(s"$MaxScopeId.$MaxFuncsInObjectId")
      errors(2).key must equal(s"$MaxScopeId.$MaxParamsInFuncId")
      errors(3).key must equal(s"$MaxScopeId.$MaxObjectsInTreeId")
      errors(4).key must equal(s"$MaxScopeId.$MaxHeightId")

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.number"))
      }
    }

    "reject if given a submission with values above the form's max value" in {
      val errors = formBuilder(
        height = "100",
        maxFuncsInObject = "100",
        maxParamsInFunc = "100",
        maxObjectsInTree = "100",
        maxHeight = "100",
        currentNode = "100"
      ).errors

      errors.length must equal(5)

      errors(0).key must equal(s"$MaxScopeId.$HeightId")
      errors(1).key must equal(s"$MaxScopeId.$MaxFuncsInObjectId")
      errors(2).key must equal(s"$MaxScopeId.$MaxParamsInFuncId")
      errors(3).key must equal(s"$MaxScopeId.$MaxObjectsInTreeId")
      errors(4).key must equal(s"$MaxScopeId.$MaxHeightId")

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.max"))
      }
    }

    "reject if given a submission with values below the form's min value" in {
      val errors = formBuilder(
        height = "-1",
        maxFuncsInObject = "-1",
        maxParamsInFunc = "-1",
        maxObjectsInTree = "-1",
        maxHeight = "-1",
        currentNode = "-1"
      ).errors

      errors.length must equal(5)

      errors(0).key must equal(s"$MaxScopeId.$HeightId")
      errors(1).key must equal(s"$MaxScopeId.$MaxFuncsInObjectId")
      errors(2).key must equal(s"$MaxScopeId.$MaxParamsInFuncId")
      errors(3).key must equal(s"$MaxScopeId.$MaxObjectsInTreeId")
      errors(4).key must equal(s"$MaxScopeId.$MaxHeightId")

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.min"))
      }
    }

    "not map ignored values if given a submission that is valid but provides values that the mapping specifies as ignored" in {
      val numVals = 42
      val numFuncs = 42
      val numObjects = 42
      val height = 4
      val maxExpressionsInFunc = 42
      val maxFuncsInObject = 6
      val maxParamsInFunc = 7
      val maxObjectsInTree = 8
      val maxHeight = 9
      val currentNode = 1
      val form = formBuilderWithIgnoredFields(
        numVals.toString,
        numFuncs.toString,
        numObjects.toString,
        height.toString,
        maxExpressionsInFunc.toString,
        maxFuncsInObject.toString,
        maxParamsInFunc.toString,
        maxObjectsInTree.toString,
        maxHeight.toString,
        currentNode.toString
      )
      form.errors.length must equal(0)
      val model = form.get
      model.maxScope.numVals must equal(0)
      model.maxScope.numFuncs must equal(0)
      model.maxScope.numObjects must equal(0)
      model.maxScope.height must equal(height)
      model.maxScope.maxExpressionsInFunc must equal(0)
      model.maxScope.maxFuncsInObject must equal(maxFuncsInObject)
      model.maxScope.maxParamsInFunc must equal(maxParamsInFunc)
      model.maxScope.maxObjectsInTree must equal(maxObjectsInTree)
      model.maxScope.maxHeight must equal(maxHeight)
    }

    "accept if given a valid submission" in {
      val height = 4
      val maxFuncsInObject = 6
      val maxParamsInFunc = 7
      val maxObjectsInTree = 8
      val maxHeight = 9
      val currentNode = 1
      val form = formBuilder(
        height.toString,
        maxFuncsInObject.toString,
        maxParamsInFunc.toString,
        maxObjectsInTree.toString,
        maxHeight.toString,
        currentNode.toString
      )
      form.errors.length must equal(0)
      val model = form.get
      model.maxScope.numVals must equal(0)
      model.maxScope.numFuncs must equal(0)
      model.maxScope.numObjects must equal(0)
      model.maxScope.height must equal(height)
      model.maxScope.maxExpressionsInFunc must equal(0)
      model.maxScope.maxFuncsInObject must equal(maxFuncsInObject)
      model.maxScope.maxParamsInFunc must equal(maxParamsInFunc)
      model.maxScope.maxObjectsInTree must equal(maxObjectsInTree)
      model.maxScope.maxHeight must equal(maxHeight)
    }
  }

  private def formBuilder(
    height: String,
    maxFuncsInObject: String,
    maxParamsInFunc: String,
    maxObjectsInTree: String,
    maxHeight: String,
    currentNode: String) = {
    populate.form.bind(
      Map(
        s"$MaxScopeId.$HeightId" -> height,
        s"$MaxScopeId.$MaxFuncsInObjectId" -> maxFuncsInObject,
        s"$MaxScopeId.$MaxParamsInFuncId" -> maxParamsInFunc,
        s"$MaxScopeId.$MaxObjectsInTreeId" -> maxObjectsInTree,
        s"$MaxScopeId.$MaxHeightId" -> maxHeight
      )
    )
  }

  private def formBuilderWithIgnoredFields(numVals: String,
    numFuncs: String,
    numObjects: String,
    height: String,
    maxExpressionsInFunc: String,
    maxFuncsInObject: String,
    maxParamsInFunc: String,
    maxObjectsInTree: String,
    maxHeight: String,
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
        s"$MaxScopeId.$MaxObjectsInTreeId" -> maxObjectsInTree,
        s"$MaxScopeId.$MaxHeightId" -> maxHeight
      )
    )
  }

  private def populate = testInjector(new StubGeneratorBinding).getInstance(classOf[Populate])
}
