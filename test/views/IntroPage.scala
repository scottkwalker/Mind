package views

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.{Page, WebBrowser}

class IntroPage(port: Int)(implicit driver: WebDriver) extends Page with WebBrowser {

  val title = "Mind - Introduction"
  val titleCy = "Mind - WELSH Introduction"

  override val url = s"http://localhost:$port/mind/intro"
}