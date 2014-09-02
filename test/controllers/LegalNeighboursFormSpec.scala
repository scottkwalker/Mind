package controllers

import composition.TestComposition
import models.common.LegalNeighboursRequest.Form.CurrentNodeId
import models.common.Scope.Form._

final class LegalNeighboursFormSpec extends TestComposition {

  "form" must {
    "reject when submission is empty" in {
      val errors = legalNeighbours.form.bind(Map("" -> "")).errors
      errors.length must equal(9)
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

      errors.length must equal(9)

      errors(0).key must equal(s"$ScopeId.$NumValsId")
      errors(1).key must equal(s"$ScopeId.$NumFuncsId")
      errors(2).key must equal(s"$ScopeId.$NumObjectsId")
      errors(3).key must equal(s"$ScopeId.$HeightId")
      errors(4).key must equal(s"$ScopeId.$MaxExpressionsInFuncId")
      errors(5).key must equal(s"$ScopeId.$MaxFuncsInObjectId")
      errors(6).key must equal(s"$ScopeId.$MaxParamsInFuncId")
      errors(7).key must equal(s"$ScopeId.$MaxObjectsInTreeId")
      errors(8).key must equal(CurrentNodeId)

      for (i <- 0 until errors.length) {
        errors(i).messages must equal(List("error.number"))
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
      model.scope.numVals must equal(numVals)
      model.scope.numFuncs must equal(numFuncs)
      model.scope.numObjects must equal(numObjects)
      model.scope.height must equal(height)
      model.scope.maxExpressionsInFunc must equal(maxExpressionsInFunc)
      model.scope.maxFuncsInObject must equal(maxFuncsInObject)
      model.scope.maxParamsInFunc must equal(maxParamsInFunc)
      model.scope.maxObjectsInTree must equal(maxObjectsInTree)
    }
  }

  private val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])

  private def formWithValidDefaults(numVals: String,
                                    numFuncs: String,
                                    numObjects: String,
                                    height: String,
                                    maxExpressionsInFunc: String,
                                    maxFuncsInObject: String,
                                    maxParamsInFunc: String,
                                    maxObjectsInTree: String,
                                    currentNode: String) = {
    legalNeighbours.form.bind(
      Map(
        s"$ScopeId.$NumValsId" -> numVals,
        s"$ScopeId.$NumFuncsId" -> numFuncs,
        s"$ScopeId.$NumObjectsId" -> numObjects,
        s"$ScopeId.$HeightId" -> height,
        s"$ScopeId.$MaxExpressionsInFuncId" -> maxExpressionsInFunc,
        s"$ScopeId.$MaxFuncsInObjectId" -> maxFuncsInObject,
        s"$ScopeId.$MaxParamsInFuncId" -> maxParamsInFunc,
        s"$ScopeId.$MaxObjectsInTreeId" -> maxObjectsInTree,
        CurrentNodeId -> currentNode
      )
    )
  }
}
