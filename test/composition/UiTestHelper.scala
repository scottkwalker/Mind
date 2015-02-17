package composition

import org.scalatest.concurrent.IntegrationPatience
import org.scalatestplus.play.HtmlUnitFactory
import org.scalatestplus.play.OneBrowserPerTest
import org.scalatestplus.play.OneServerPerSuite

trait UiTestHelper extends UnitTestHelpers with IntegrationPatience with OneServerPerSuite with OneBrowserPerTest with HtmlUnitFactory
