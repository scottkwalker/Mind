package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class ValueSpec extends Specification {
  "Value" should {
    "canTerminate" in {
      Value("a").canTerminate(1) mustEqual true
    }
    
    "validate" in {
      Value("a").validate mustEqual true
    }
    
    "toRawScala" in {
      Value("a").toRawScala mustEqual "a"
    }
  }
}