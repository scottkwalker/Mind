package views

import models.common.LegalNeighboursRequest.Form.CurrentNodeId
import models.common.Scope.Form._
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

class LegalNeighboursPage(implicit driver: WebDriver) extends WebBrowser {

  val title = "Mind - Legal neighbours calculator"
  val titleCy = "Mind - Cyfrifiannell cymdogion Cyfreithiol"
  val validationSummary = """<div id="validation-summary">"""

  def url(port: Int) = s"http://localhost:$port/mind/legal-neighbours"

  def currentNode = textField(CurrentNodeId)

  def numVals = textField(s"$ScopeId.$NumValsId")

  def numFuncs = textField(s"$ScopeId.$NumFuncsId")

  def numObjects = textField(s"$ScopeId.$NumObjectsId")

  def height = textField(s"$ScopeId.$HeightId")

  def maxExpressionsInFunc = textField(s"$ScopeId.$MaxExpressionsInFuncId")

  def maxFuncsInObject = textField(s"$ScopeId.$MaxFuncsInObjectId")

  def maxParamsInFunc = textField(s"$ScopeId.$MaxParamsInFuncId")

  def maxObjectsInTree = textField(s"$ScopeId.$MaxObjectsInTreeId")
}