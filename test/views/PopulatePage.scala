package views

import models.common.Scope.Form._
import models.common.PopulateRequest.Form.MaxScopeId
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.{Page, WebBrowser}

class PopulatePage(port: Int)(implicit driver: WebDriver) extends Page with WebBrowser {

  val title = "Mind - Populate"
  val titleCy = "Mind - Boblogi"
  val validationSummary = """<div id="validation-summary">"""

  override val url = s"http://localhost:$port/mind/populate"

  def numVals = textField(s"$MaxScopeId.$NumValsId")

  def numFuncs = textField(s"$MaxScopeId.$NumFuncsId")

  def numObjects = textField(s"$MaxScopeId.$NumObjectsId")

  def height = textField(s"$MaxScopeId.$HeightId")

  def maxExpressionsInFunc = textField(s"$MaxScopeId.$MaxExpressionsInFuncId")

  def maxFuncsInObject = textField(s"$MaxScopeId.$MaxFuncsInObjectId")

  def maxParamsInFunc = textField(s"$MaxScopeId.$MaxParamsInFuncId")

  def maxObjectsInTree = textField(s"$MaxScopeId.$MaxObjectsInTreeId")
}
