package views

import composition.TestComposition
import org.scalatestplus.play._
import play.api.Play
import play.api.test.WithApplication

final class IntroUiSpec extends TestComposition with OneServerPerSuite with OneBrowserPerTest with HtmlUnitFactory {

  "go to page" must {
    "display the page in English when no language cookie exists" in new WithApplication {
      val page = new IntroPage(port)

      go to page

      eventually(timeout = browserTimeout) {
        pageTitle mustBe page.title
      }
    }
  }
}