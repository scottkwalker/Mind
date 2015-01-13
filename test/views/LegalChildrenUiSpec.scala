package views

import composition.TestComposition
import org.scalatestplus.play._
import play.api.Play
import play.api.libs.json.{JsArray, JsNumber}
import play.api.test.WithApplication
import replaceEmpty.AddOperatorFactoryImpl

final class LegalChildrenUiSpec extends TestComposition with OneServerPerSuite with OneBrowserPerTest with HtmlUnitFactory {

  // To enable testing on all browsers https://www.playframework.com/documentation/2.2.x/ScalaFunctionalTestingWithScalaTest
  //with OneServerPerSuite with AllBrowsersPerSuite {
  //
  //  def sharedTests(browser: BrowserInfo) = {
  //  "go to page" must {
  //    "display the page " + browser.name in {
  //      go to s"http://localhost:$port/mind/legal-children"
  //      pageTitle mustBe "Mind - Legal neighbours calculator"
  //    }
  //  }
  //  }


  "go to page" must {
    "display the page in English when no language cookie exists" taggedAs UiTag in new WithApplication {
      val page = new LegalChildrenPage(port)

      go to page

      eventually(timeout = browserTimeout) {
        pageTitle mustBe page.title
      }
    }

    "display the page in Welsh when language cookie contains 'cy'" taggedAs UiTag in new WithApplication {
      val page = new LegalChildrenPage(port)
      go to page
      // Must be on a page before you can set a cookie.
      val key = Play.langCookieName
      val value = "cy" // Code for Welsh
      add cookie(key, value)

      go to page

      eventually(timeout = browserTimeout) {
        pageTitle mustBe page.titleCy
      }
    }
  }

  "submit button" must {
    "return expected json when valid data is submitted" taggedAs UiTag in new WithApplication {
      val page = new LegalChildrenPage(port)
      val expected = JsArray(Seq(JsNumber(7))).toString()
      val valid = "1"
      go to page
      // Fill in the fields
      page.currentNode.value = AddOperatorFactoryImpl.id.value.toString
      page.numVals.value = valid
      page.numFuncs.value = valid
      page.numObjects.value = valid
      page.height.value = valid
      page.maxExpressionsInFunc.value = valid
      page.maxFuncsInObject.value = valid
      page.maxParamsInFunc.value = valid
      page.maxObjectsInTree.value = valid
      page.maxHeight.value = valid

      submit()

      eventually(timeout = browserTimeout) {
        pageSource must equal(expected)
      }
    }

    "display validation error messages when invalid data is submitted " taggedAs UiTag in new WithApplication {
      val page = new LegalChildrenPage(port)
      val invalid = "-1"
      go to page
      page.numVals.value = invalid

      submit()

      eventually(timeout = browserTimeout) {
        pageSource must include(page.validationSummary)
      }
    }
  }
}