package views

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.Page
import org.scalatest.selenium.WebBrowser

class HealthCheckPage(port: Int)(implicit driver: WebDriver) extends Page with WebBrowser {

  val title = "Mind - Health check"
  val titleCy = "Mind - Archwiliad iechyd"

  override val url = s"http://localhost:$port/mind/health-check"
}
