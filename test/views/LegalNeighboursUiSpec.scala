package views

import org.scalatestplus.play._
import views.LegalNeighbours.SubmitId

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
    // TODO implement
//    "return expected json when valid data is submitted" in {
//      go to s"http://localhost:$port/mind/legal-neighbours"
//      click on find(id(SubmitId)).value
//      eventually {
//        pageSource must include("TODO expected json goes here")
//      }
//    }

    "display validation error messages when no data is submitted " in {
      go to s"http://localhost:$port/mind/legal-neighbours"
      click on find(id(SubmitId)).value
      eventually {
        pageSource must include( """<div id="validation-summary">""")
      }
    }
  }
}
