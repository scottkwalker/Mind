package views

import composition.TestComposition
import org.scalatestplus.play._
import play.api.Play
import play.api.libs.json.{JsArray, JsNumber}
import play.api.test.WithApplication
import replaceEmpty.AddOperatorFactoryImpl

final class PopulateUiSpec extends TestComposition with OneServerPerSuite with OneBrowserPerTest with HtmlUnitFactory {

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


//  "go to page" must {
//    "display the page in English when no language cookie exists" taggedAs UiTag in new WithApplication {
//      val page = new PopulatePage(port)
//
//      go to page
//
//      eventually(timeout = browserTimeout) {
//        pageTitle mustBe page.title
//      }
//    }
//
//    "display the page in Welsh when language cookie contains 'cy'" taggedAs UiTag in new WithApplication {
//      val page = new PopulatePage(port)
//      go to page
//      // Must be on a page before you can set a cookie.
//      val key = Play.langCookieName
//      val value = "cy" // Code for Welsh
//      add cookie(key, value)
//
//      go to page
//
//      eventually(timeout = browserTimeout) {
//        pageTitle mustBe page.titleCy
//      }
//    }
//  }

  "submit button" must {
    "return expected json when valid data is submitted" taggedAs UiTag in new WithApplication {
      val page = new PopulatePage(port)
      val expected = "repository now contains 2"
      val valid = "1"
      go to page
      // Fill in the fields
      page.height.value = valid
      page.maxFuncsInObject.value = "0"
      page.maxParamsInFunc.value = "0"
      page.maxObjectsInTree.value = "0"
      page.maxHeight.value = valid

      submit()

      eventually {
        pageSource must equal(expected)
      }(config = whenReadyPatienceConfig)
    }

//    "display validation error messages when invalid data is submitted " taggedAs UiTag in new WithApplication {
//      val page = new PopulatePage(port)
//      val invalid = "-1"
//      go to page
//      page.maxFuncsInObject.value = invalid
//
//      submit()
//
//      eventually(timeout = browserTimeout) {
//        pageSource must include(page.validationSummary)
//      }
//    }
  }
}