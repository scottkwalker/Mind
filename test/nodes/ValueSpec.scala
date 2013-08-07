package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class ValueSpec extends Specification {
  "Value" should {
    "canTerminate" in {
      Value("a").canTerminate(1) mustEqual true
    }
    
    "hasNoEmptyNodes true" in {
      Value("a").hasNoEmptyNodes mustEqual true
    }
    
    "toRawScala" in {
      Value("a").toRawScala mustEqual "a"
    }
    
    "validate true when given a non-empty name" in {
      Value("a").validate mustEqual true
    }    
    
    "validate false when given an empty name" in {
      Value("").validate mustEqual false
    }
  }
}