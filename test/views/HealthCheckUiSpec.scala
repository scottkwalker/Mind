package views

import composition.ApplicationWithTestGlobal
import composition.TestComposition
import composition.UiTestHelper
import play.api.Play

final class HealthCheckUiSpec extends UiTestHelper {

  "go to page" must {
    "display the page in English when no language cookie exists" taggedAs UiTag in new ApplicationWithTestGlobal with TestComposition {
      val page = new HealthCheckPage(port)

      go to page

      eventually {
        pageTitle mustBe page.title
      }(config = patienceConfig)
    }

    "display the page in Welsh when language cookie contains 'cy'" taggedAs UiTag in new ApplicationWithTestGlobal with TestComposition {
      val page = new HealthCheckPage(port)
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
}