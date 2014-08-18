package utils.helpers

import akka.util.Timeout
import composition.TestComposition
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.duration.{FiniteDuration, SECONDS}

abstract class UnitSpec extends WordSpec with Matchers with MockitoSugar with ScalaFutures with TestComposition {

  protected implicit val timeout = Timeout(FiniteDuration(1, SECONDS))
}