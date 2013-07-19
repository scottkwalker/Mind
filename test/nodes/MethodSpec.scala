package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class MethodSpec extends Specification {
  "Method" should {
    "is not terminal" in {
      Method(Seq(AddOperator(Value("a"), Value("b")))).isTerminal mustEqual false
    }
    
    "canTerminate in 3 steps" in {
      Method(Seq(AddOperator(Value("a"), Value("b")))).canTerminate(3) mustEqual true
    }
    
    "can not terminate in 2 steps" in {
      Method(Seq(AddOperator(Value("a"), Value("b")))).canTerminate(2) mustEqual false
    }
    
    "validate" in {
      Method(Seq(AddOperator(Value("a"), Value("b")))).validate mustEqual true
    }
    
    "toRawScala" in {
      Method(Seq(AddOperator(Value("a"), Value("b")))).toRawScala mustEqual "def f1(a: Int, b: Int) = { a + b }"
    }
  }
}