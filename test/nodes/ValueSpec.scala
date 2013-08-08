package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class ValueSpec extends Specification {
  "Value" should {
    "canTerminate" in {
      Value("a").canTerminate(1) mustEqual true
    }

    "toRawScala" in {
      Value("a").toRawScala mustEqual "a"
    }

    "validate" in {
      "true given a non-empty name" in {
        Value("a").validate mustEqual true
      }

      "false given an empty name" in {
        Value("").validate mustEqual false
      }
    }
  }
}