package views

import composition.TestComposition
import models.common.LegalNeighboursRequest.Form._
import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.support.ui.{ExpectedConditions, ExpectedCondition, WebDriverWait}
import org.scalatestplus.play._
import play.api.Play
import play.api.libs.json.{JsArray, JsNumber}
import play.api.test.WithApplication
import replaceEmpty.AddOperatorFactoryImpl

final class LegalNeighboursUiSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerTest with HtmlUnitFactory with TestComposition {

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
    "display the page in English when no language cookie exists" in new WithApplication {
      val page = new LegalNeighboursPage

      go to page.url(port)

      eventually(timeout = browserTimeout) {
        pageTitle mustBe page.title
      }
    }

    "display the page in Welsh when language cookie contains 'cy'" in new WithApplication {
      val page = new LegalNeighboursPage
      go to s"http://localhost:$port"
      val key = Play.langCookieName
      val value = "cy" // Code for Welsh
      add cookie(key, value)

      go to page.url(port)

      eventually(timeout = browserTimeout) {
        pageTitle mustBe page.titleCy
      }
    }
  }

  "submit button" must {
    "return expected json when valid data is submitted" in new WithApplication {
      val page = new LegalNeighboursPage
      val expected = JsArray(Seq(JsNumber(7))).toString()
      val valid = "1"
      go to page.url(port)
      // Fill in the fields
      page.currentNode.value = AddOperatorFactoryImpl.id.toString
      page.numVals.value = valid
      page.numFuncs.value = valid
      page.numObjects.value = valid
      page.height.value = valid
      page.maxExpressionsInFunc.value = valid
      page.maxFuncsInObject.value = valid
      page.maxParamsInFunc.value = valid
      page.maxObjectsInTree.value = valid

      submit()

      eventually(timeout = browserTimeout) {
        pageSource must equal(expected)
      }
    }

    "display validation error messages when no data is submitted " in new WithApplication {
      val page = new LegalNeighboursPage
      val invalid = "-1"
      go to page.url(port)
      page.numVals.value = invalid

      submit()

      eventually(timeout = browserTimeout) {
        pageSource must include(page.validationSummary)
      }
    }
  }
}