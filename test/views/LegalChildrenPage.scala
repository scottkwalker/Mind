package views

import models.common.LookupChildrenRequest.Form.CurrentNodeId
import models.common.LookupChildrenRequest.Form.ScopeId
import models.common.Scope.Form._
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.Page
import org.scalatest.selenium.WebBrowser

class LegalChildrenPage(port: Int)(implicit driver: WebDriver) extends Page with WebBrowser {

  override val url = s"http://localhost:$port/mind/legal-children"
  val title = "Mind - Legal children calculator"
  val titleCy = "Mind - Cyfrifiannell cymdogion Cyfreithiol"
  val validationSummary = """<div id="validation-summary">"""

  def currentNode = textField(CurrentNodeId)

  def numVals = textField(s"$ScopeId.$NumValsId")

  def numFuncs = textField(s"$ScopeId.$NumFuncsId")

  def numObjects = textField(s"$ScopeId.$NumObjectsId")

  def height = textField(s"$ScopeId.$HeightId")

  def maxExpressionsInFunc = textField(s"$ScopeId.$MaxExpressionsInFuncId")

  def maxFuncsInObject = textField(s"$ScopeId.$MaxFuncsInObjectId")

  def maxParamsInFunc = textField(s"$ScopeId.$MaxParamsInFuncId")

  def maxObjectsInTree = textField(s"$ScopeId.$MaxObjectsInTreeId")

  def maxHeight = textField(s"$ScopeId.$MaxHeightId")
}
