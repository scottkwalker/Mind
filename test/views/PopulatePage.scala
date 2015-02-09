package views

import models.common.PopulateRequest.Form.MaxScopeId
import models.common.Scope.Form._
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.Page
import org.scalatest.selenium.WebBrowser

class PopulatePage(port: Int)(implicit driver: WebDriver) extends Page with WebBrowser {

  val title = "Mind - Populate"
  val titleCy = "Mind - Boblogi"
  val validationSummary = """<div id="validation-summary">"""

  override val url = s"http://localhost:$port/mind/populate"

  def height = textField(id(s"${MaxScopeId}_$HeightId"))

  def maxFuncsInObject = textField(id(s"${MaxScopeId}_$MaxFuncsInObjectId"))

  def maxParamsInFunc = textField(id(s"${MaxScopeId}_$MaxParamsInFuncId"))

  def maxObjectsInTree = textField(id(s"${MaxScopeId}_$MaxObjectsInTreeId"))

  def maxHeight = textField(id(s"${MaxScopeId}_$MaxHeightId"))
}
