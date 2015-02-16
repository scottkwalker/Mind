package views

import composition.TestComposition
import composition.WithApplication
import org.scalatest.concurrent.IntegrationPatience
import org.scalatestplus.play.HtmlUnitFactory
import org.scalatestplus.play.OneBrowserPerTest
import org.scalatestplus.play.OneServerPerSuite
import play.api.Play

final class IntroUiSpec extends TestComposition with IntegrationPatience with OneServerPerSuite with OneBrowserPerTest with HtmlUnitFactory {

  "go to page" must {
    "display the page in English when no language cookie exists" taggedAs UiTag in new WithApplication(testInjector = testInjector()) {
      val page = new IntroPage(port)

      go to page

      eventually {
        pageTitle mustBe page.title
      }(config = patienceConfig)
    }
  }

  "display the page in Welsh when language cookie contains 'cy'" taggedAs UiTag in new WithApplication(testInjector = testInjector()) {
    val page = new IntroPage(port)
    go to page
    // Must be on a page before you can set a cookie.
    val key = Play.langCookieName
    val value = "cy" // Code for Welsh
    add cookie(key, value)

    go to page

    eventually {
      pageTitle mustBe page.titleCy
    }(config = patienceConfig)
  }
}