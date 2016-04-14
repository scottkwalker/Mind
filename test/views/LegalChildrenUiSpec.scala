package views

import composition.ApplicationWithTestGlobal
import composition.TestComposition
import composition.UiTestHelper
import decision.AddOperatorFactory
import play.api.Play
import play.api.libs.json.JsArray

final class LegalChildrenUiSpec extends UiTestHelper {

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
    "display the page in English when no language cookie exists" taggedAs UiTag in new ApplicationWithTestGlobal with TestComposition {
      val page = new LegalChildrenPage(port)

      go to page

      eventually {
        pageTitle mustBe page.title
      }(config = patienceConfig)
    }

    "display the page in Welsh when language cookie contains 'cy'" taggedAs UiTag in new ApplicationWithTestGlobal with TestComposition {
      val page = new LegalChildrenPage(port)
      go to page
      // Must be on a page before you can set a cookie.
      val key = Play.langCookieName
      val value = "cy" // Code for Welsh
      add cookie (key, value)

      go to page

      eventually {
        pageTitle mustBe page.titleCy
      }(config = patienceConfig)
    }
  }

  "submit button" must {
    "return empty json when valid data is submitted but nothing is in the cache" taggedAs UiTag in new ApplicationWithTestGlobal with TestComposition {
      val page = new LegalChildrenPage(port)
      val expected = JsArray(Seq.empty).toString()
      val valid = "1"
      go to page
      // Fill in the fields
      page.currentNode.value = AddOperatorFactory.id.value.toString
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

      eventually {
        pageSource must equal(expected)
      }(config = patienceConfig)
    }

    //    "return expected json when valid data is submitted" taggedAs UiTag in new WithApplication2 with TestComposition2 {
    //      val page = new LegalChildrenPage(port)
    //      val expected = JsArray(Seq(JsNumber(7))).toString()
    //      val valid = "1"
    //      go to page
    //      // Fill in the fields
    //      page.currentNode.value = AddOperatorFactory.id.value.toString
    //      page.numVals.value = valid
    //      page.numFuncs.value = valid
    //      page.numObjects.value = valid
    //      page.height.value = valid
    //      page.maxExpressionsInFunc.value = valid
    //      page.maxFuncsInObject.value = valid
    //      page.maxParamsInFunc.value = valid
    //      page.maxObjectsInTree.value = valid
    //      page.maxHeight.value = valid
    //
    //      submit()
    //
    //      eventually {
    //        pageSource must equal(expected)
    //      }(config = whenReadyPatienceConfig)
    //    }

    "display validation error messages when invalid data is submitted " taggedAs UiTag in new ApplicationWithTestGlobal with TestComposition {
      val page = new LegalChildrenPage(port)
      val invalid = "-1"
      go to page
      page.numVals.value = invalid

      submit()

      eventually {
        pageSource must include(page.validationSummary)
      }(config = patienceConfig)
    }
  }
}