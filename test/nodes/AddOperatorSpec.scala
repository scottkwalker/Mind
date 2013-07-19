package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class AddOperatorSpec extends Specification {
  "Value" should {
    "is not terminal" in {
      AddOperator(Value("a"), Value("b")).isTerminal mustEqual false
    }
    
    "canTerminate" in {
      AddOperator(Value("a"), Value("b")).canTerminate(1) mustEqual true
    }
    
    "validate" in {
      AddOperator(Value("a"), Value("b")).validate mustEqual true
    }
    
    "toRawScala" in {
      AddOperator(Value("a"), Value("b")).toRawScala mustEqual "a + b"
    }
  }
}