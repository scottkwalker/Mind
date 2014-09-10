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
      go to LegalNeighboursPage.url(port)
      // Fill in the fields
      currentNode.value = AddOperatorFactoryImpl.id.toString
      numVals.value = "1"
      numFuncs.value = "1"
      numObjects.value = "1"
      height.value = "1"
      maxExpressionsInFunc.value = "1"
      maxFuncsInObject.value = "1"
      maxParamsInFunc.value = "1"
      maxObjectsInTree.value = "1"
      submit()

      eventually {
        pageSource must equal(expected)
      }
    }

    "display validation error messages when no data is submitted " in {
      go to LegalNeighboursPage.url(port)
      numVals.value = "-1"
      submit()
      pageSource must include(validationSummary)
    }
  }
}