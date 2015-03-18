package controllers

import composition.StubLookupChildrenBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.LookupChildrenRequest.Form.CurrentNodeId
import models.common.LookupChildrenRequest.Form.ScopeId
import models.common.Scope.Form._
import models.common.LookupChildrenRequest.Form.CurrentNodeMax
import models.common.LookupChildrenRequest.Form.CurrentNodeMin

final class LegalChildrenFormSpec extends UnitTestHelpers with TestComposition {

  "form" must {
    "reject if submission is empty" in {
      val errors = legalChildren.form.bind(Map("" -> "")).errors
      errors.length must equal(10)
    }

    "reject if the submission contains wrong types e.g. strings instead of integers" in {
      val invalid = "INVALID"
      val errors = formBuilder(
        numVals = invalid,
        numFuncs = invalid,
        numObjects = invalid,
        height = invalid,
        maxExpressionsInFunc = invalid,
        maxFuncsInObject = invalid,
        maxParamsInFunc = invalid,
        maxObjectsInTree = invalid,
        maxHeight = invalid,
        currentNode = invalid
      ).errors

      errors.length must equal(10)

      errors(0).key must equal(s"$ScopeId.$NumValsId")
      errors(1).key must equal(s"$ScopeId.$NumFuncsId")
      errors(2).key must equal(s"$ScopeId.$NumObjectsId")
      errors(3).key must equal(s"$ScopeId.$HeightId")
      errors(4).key must equal(s"$ScopeId.$MaxExpressionsInFuncId")
      errors(5).key must equal(s"$ScopeId.$MaxFuncsInObjectId")
      errors(6).key must equal(s"$ScopeId.$MaxParamsInFuncId")
      errors(7).key must equal(s"$ScopeId.$MaxObjectsInTreeId")
      errors(8).key must equal(s"$ScopeId.$MaxHeightId")
      errors(9).key must equal(CurrentNodeId)

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.number"))
      }
    }

    "reject if the submission contains values above the form's max boundary" in {
      val tooLarge = (CurrentNodeMax + 1).toString
      val errors = formBuilder(
        numVals = tooLarge,
        numFuncs = tooLarge,
        numObjects = tooLarge,
        height = tooLarge,
        maxExpressionsInFunc = tooLarge,
        maxFuncsInObject = tooLarge,
        maxParamsInFunc = tooLarge,
        maxObjectsInTree = tooLarge,
        maxHeight = tooLarge,
        currentNode = tooLarge
      ).errors

      errors.length must equal(10)

      errors(0).key must equal(s"$ScopeId.$NumValsId")
      errors(1).key must equal(s"$ScopeId.$NumFuncsId")
      errors(2).key must equal(s"$ScopeId.$NumObjectsId")
      errors(3).key must equal(s"$ScopeId.$HeightId")
      errors(4).key must equal(s"$ScopeId.$MaxExpressionsInFuncId")
      errors(5).key must equal(s"$ScopeId.$MaxFuncsInObjectId")
      errors(6).key must equal(s"$ScopeId.$MaxParamsInFuncId")
      errors(7).key must equal(s"$ScopeId.$MaxObjectsInTreeId")
      errors(8).key must equal(s"$ScopeId.$MaxHeightId")
      errors(9).key must equal(CurrentNodeId)

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.max"))
      }
    }

    "reject if the submission contains values below the form's min value" in {
      val toSmall = (CurrentNodeMin - 1).toString
      val errors = formBuilder(
        numVals = toSmall,
        numFuncs = toSmall,
        numObjects = toSmall,
        height = toSmall,
        maxExpressionsInFunc = toSmall,
        maxFuncsInObject = toSmall,
        maxParamsInFunc = toSmall,
        maxObjectsInTree = toSmall,
        maxHeight = toSmall,
        currentNode = toSmall
      ).errors

      errors.length must equal(10)

      errors(0).key must equal(s"$ScopeId.$NumValsId")
      errors(1).key must equal(s"$ScopeId.$NumFuncsId")
      errors(2).key must equal(s"$ScopeId.$NumObjectsId")
      errors(3).key must equal(s"$ScopeId.$HeightId")
      errors(4).key must equal(s"$ScopeId.$MaxExpressionsInFuncId")
      errors(5).key must equal(s"$ScopeId.$MaxFuncsInObjectId")
      errors(6).key must equal(s"$ScopeId.$MaxParamsInFuncId")
      errors(7).key must equal(s"$ScopeId.$MaxObjectsInTreeId")
      errors(8).key must equal(s"$ScopeId.$MaxHeightId")
      errors(9).key must equal(CurrentNodeId)

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.min"))
      }
    }

    "accept if given a valid submission" in {
      val numVals = 1
      val numFuncs = 2
      val numObjects = 3
      val height = 4
      val maxExpressionsInFunc = 5
      val maxFuncsInObject = 6
      val maxParamsInFunc = 7
      val maxObjectsInTree = 8
      val maxHeight = 9
      val currentNode = 1
      val form = formBuilder(
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
      model.scope.numVals must equal(numVals)
      model.scope.numFuncs must equal(numFuncs)
      model.scope.numObjects must equal(numObjects)
      model.scope.height must equal(height)
      model.scope.maxExpressionsInFunc must equal(maxExpressionsInFunc)
      model.scope.maxFuncsInObject must equal(maxFuncsInObject)
      model.scope.maxParamsInFunc must equal(maxParamsInFunc)
      model.scope.maxObjectsInTree must equal(maxObjectsInTree)
      model.scope.maxHeight must equal(maxHeight)
    }
  }

  private def formBuilder(numVals: String,
                          numFuncs: String,
                          numObjects: String,
                          height: String,
                          maxExpressionsInFunc: String,
                          maxFuncsInObject: String,
                          maxParamsInFunc: String,
                          maxObjectsInTree: String,
                          maxHeight: String,
                          currentNode: String) = {
    legalChildren.form.bind(
      Map(
        s"$ScopeId.$NumValsId" -> numVals,
        s"$ScopeId.$NumFuncsId" -> numFuncs,
        s"$ScopeId.$NumObjectsId" -> numObjects,
        s"$ScopeId.$HeightId" -> height,
        s"$ScopeId.$MaxExpressionsInFuncId" -> maxExpressionsInFunc,
        s"$ScopeId.$MaxFuncsInObjectId" -> maxFuncsInObject,
        s"$ScopeId.$MaxParamsInFuncId" -> maxParamsInFunc,
        s"$ScopeId.$MaxObjectsInTreeId" -> maxObjectsInTree,
        s"$ScopeId.$MaxHeightId" -> maxHeight,
        CurrentNodeId -> currentNode
      )
    )
  }

  private def legalChildren = testInjector(new StubLookupChildrenBinding).getInstance(classOf[LegalChildren])
}
