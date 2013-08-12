package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class ValueMSpec extends Specification {
  "ValueM" should {
    "toRawScala" in {
      ValueM("a").toRawScala mustEqual "a"
    }

    "validate" in {
      "true given it can terminates in under N steps" in {
        ValueM("a").validate(1) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        ValueM("a").validate(0) mustEqual false
      }

      "true given a non-empty name" in {
        ValueM("a").validate(10) mustEqual true
      }

      "false given an empty name" in {
        ValueM("").validate(10) mustEqual false
      }
    }
    
    "create returns instance of this type" in {
      ValueM.create must beAnInstanceOf[ValueM]
    }
  }
}