package views

import factory.AddOperatorFactoryImpl
import models.common.LegalNeighboursRequest.Form.CurrentNodeId
import org.scalatestplus.play._
import play.api.Play
import play.api.libs.json.{JsArray, JsNumber}
import views.LegalNeighboursPage._

final class LegalNeighboursUiSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerTest with HtmlUnitFactory {

  // To enable testing on all browsers https://www.playframework.com/documentation/2.2.x/ScalaFunctionalTestingWithScalaTest
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
    "display the page in English when no language cookie exists" in {
      go to LegalNeighboursPage.url(port)
      pageTitle mustBe LegalNeighboursPage.title
    }

    "display the page in Welsh when language cookie contains 'cy'" in {
      go to s"http://localhost:$port"
      val key = Play.langCookieName
      val value = "cy" // Code for Welsh
      add cookie(key, value)
      go to LegalNeighboursPage.url(port)
      pageTitle mustBe LegalNeighboursPage.titleCy
    }
  }

  "submit button" must {
    "return expected json when valid data is submitted" in {
      val expected = JsArray(Seq(JsNumber(7))).toString()
      val valid = "1"
      go to LegalNeighboursPage.url(port)
      // Fill in the fields
      currentNode.value = AddOperatorFactoryImpl.id.toString
      numVals.value = valid
      numFuncs.value = valid
      numObjects.value = valid
      height.value = valid
      maxExpressionsInFunc.value = valid
      maxFuncsInObject.value = valid
      maxParamsInFunc.value = valid
      maxObjectsInTree.value = valid
      submit()

      eventually {
        pageSource must equal(expected)
      }
    }

    "display validation error messages when no data is submitted " in {
      val invalid = "-1"
      go to LegalNeighboursPage.url(port)
      numVals.value = invalid
      submit()
      pageSource must include(validationSummary)
    }
  }
}