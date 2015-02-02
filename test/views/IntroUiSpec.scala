package views

import composition.TestComposition
import org.scalatestplus.play._
import play.api.Play
import play.api.test.WithApplication

final class IntroUiSpec extends TestComposition with OneServerPerSuite with OneBrowserPerTest with HtmlUnitFactory {

  "go to page" must {
    "display the page in English when no language cookie exists" taggedAs UiTag in new WithApplication {
      val page = new IntroPage(port)

      go to page

      eventually {
        pageTitle mustBe page.title
      }(config = whenReadyPatienceConfig)
    }
  }

  "display the page in Welsh when language cookie contains 'cy'" taggedAs UiTag in new WithApplication {
    val page = new IntroPage(port)
    go to page
    // Must be on a page before you can set a cookie.
    val key = Play.langCookieName
    val value = "cy" // Code for Welsh
    add cookie(key, value)

    go to page

    eventually {
      pageTitle mustBe page.titleCy
    }(config = whenReadyPatienceConfig)
  }
}