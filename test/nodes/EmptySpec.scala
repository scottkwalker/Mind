package nodes

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class EmptySpec extends Specification {
  "Empty" should {
    "be terminal" in {
      Empty().isTerminal mustEqual true
    }
  }
}