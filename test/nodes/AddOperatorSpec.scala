package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class AddOperatorSpec extends Specification {
  "AddOperator" should {
    "is not terminal" in {
      AddOperator(Value("a"), Value("b")).isTerminal mustEqual false
    }
    
    "canTerminate in 2 steps" in {
      AddOperator(Value("a"), Value("b")).canTerminate(2) mustEqual true
    }
    
    "can not terminate in 1 steps" in {
      AddOperator(Value("a"), Value("b")).canTerminate(1) mustEqual false
    }
    
    "validate" in {
      AddOperator(Value("a"), Value("b")).validate mustEqual true
    }
    
    "toRawScala" in {
      AddOperator(Value("a"), Value("b")).toRawScala mustEqual "a + b"
    }
  }
}