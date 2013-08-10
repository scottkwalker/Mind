package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class ValueMSpec extends Specification {
  "ValueM" should {
    "canTerminate" in {
      ValueM("a").canTerminate(1) mustEqual true
    }

    "toRawScala" in {
      ValueM("a").toRawScala mustEqual "a"
    }

    "validate" in {
      "true given a non-empty name" in {
        ValueM("a").validate(10) mustEqual true
      }

      "false given an empty name" in {
        ValueM("").validate(10) mustEqual false
      }
    }
  }
}