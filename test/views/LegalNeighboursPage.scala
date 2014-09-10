package views

import models.common.LegalNeighboursRequest.Form.CurrentNodeId
import models.common.Scope.Form._
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

object LegalNeighboursPage extends WebBrowser {

  val title = "Mind - Legal neighbours calculator"
  val titleCy = "Mind - Cyfrifiannell cymdogion Cyfreithiol"
  val validationSummary = """<div id="validation-summary">"""

  def url(port: Int) = s"http://localhost:$port/mind/legal-neighbours"

  def currentNode(implicit driver: WebDriver) = textField(CurrentNodeId)

  def numVals(implicit driver: WebDriver) = textField(s"$ScopeId.$NumValsId")

  def numFuncs(implicit driver: WebDriver) = textField(s"$ScopeId.$NumFuncsId")

  def numObjects(implicit driver: WebDriver) = textField(s"$ScopeId.$NumObjectsId")

  def height(implicit driver: WebDriver) = textField(s"$ScopeId.$HeightId")

  def maxExpressionsInFunc(implicit driver: WebDriver) = textField(s"$ScopeId.$MaxExpressionsInFuncId")

  def maxFuncsInObject(implicit driver: WebDriver) = textField(s"$ScopeId.$MaxFuncsInObjectId")

  def maxParamsInFunc(implicit driver: WebDriver) = textField(s"$ScopeId.$MaxParamsInFuncId")

  def maxObjectsInTree(implicit driver: WebDriver) = textField(s"$ScopeId.$MaxObjectsInTreeId")
}
