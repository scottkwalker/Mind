package views

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.Page
import org.scalatest.selenium.WebBrowser

class IntroPage(port: Int)(implicit driver: WebDriver) extends Page with WebBrowser {

  override val url = s"http://localhost:$port/mind/intro"
  val title = "Mind - Introduction"
  val titleCy = "Mind - WELSH Introduction"
}
