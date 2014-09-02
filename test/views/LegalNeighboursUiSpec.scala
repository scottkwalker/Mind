package views

import factory.AddOperatorFactoryImpl
import models.common.LegalNeighboursRequest.Form.CurrentNodeId
import models.common.Scope.Form.{heightId, maxExpressionsInFuncId, maxFuncsInObjectId, maxObjectsInTreeId, maxParamsInFuncId, numFuncsId, numObjectsId, numValsId}
import org.scalatestplus.play._
import play.api.libs.json.{JsNumber, JsArray}
import views.LegalNeighbours.SubmitId
import models.common.Scope.Form.scopeId

final class LegalNeighboursUiSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

  // TODO enable testing on all browsers https://www.playframework.com/documentation/2.2.x/ScalaFunctionalTestingWithScalaTest
  //with OneServerPerSuite with AllBrowsersPerSuite {
  //
  //  def sharedTests(browser: BrowserInfo) = {
  //  "go to page" must {
  //    "display the page " + browser.name in {
  //      go to s"http://localhost:$port/mind/legal-neighbours"
  //      pageTitle mustBe "Mind - Legal neighbours calculator"
  //    }
  //  }
  //  }

  "go to page" must {
    "display the page " in {
      go to s"http://localhost:$port/mind/legal-neighbours"
      pageTitle mustBe "Mind - Legal neighbours calculator"
    }
  }

  "submit button" must {
    "return expected json when valid data is submitted" in {
      val expected = JsArray(Seq(JsNumber(7))).toString()
      go to s"http://localhost:$port/mind/legal-neighbours"

      // Fill in the fields
      // TODO make a page object for this view and create DSL for each field.
      textField(CurrentNodeId).value = AddOperatorFactoryImpl.id.toString
      textField(s"$scopeId.$numValsId").value = "1"
      textField(s"$scopeId.$numFuncsId").value = "1"
      textField(s"$scopeId.$numObjectsId").value = "1"
      textField(s"$scopeId.$heightId").value = "1"
      textField(s"$scopeId.$maxExpressionsInFuncId").value = "1"
      textField(s"$scopeId.$maxFuncsInObjectId").value = "1"
      textField(s"$scopeId.$maxParamsInFuncId").value = "1"
      textField(s"$scopeId.$maxObjectsInTreeId").value = "1"

      click on find(id(SubmitId)).value

      eventually {
        pageSource must equal(expected)
      }
    }

    "display validation error messages when no data is submitted " in {
      go to s"http://localhost:$port/mind/legal-neighbours"
      click on find(id(SubmitId)).value
      eventually {
        pageSource must include( """<div id="validation-summary">""")
      }
    }
  }
}
