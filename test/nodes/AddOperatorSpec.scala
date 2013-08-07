package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class AddOperatorSpec extends Specification {
  "AddOperator" should {
    "canTerminate in 2 steps" in {
      AddOperator(Value("a"), Value("b")).canTerminate(2) mustEqual true
    }
    
    "can not terminate in 1 steps" in {
      AddOperator(Value("a"), Value("b")).canTerminate(1) mustEqual false
    }
    
    "hasNoEmptyNodes true when none empty" in {
      AddOperator(Value("a"), Value("b")).hasNoEmptyNodes mustEqual true
    }
    
    "hasNoEmptyNodes false when contains an empty node" in {
      AddOperator(Value("a"), Empty()).hasNoEmptyNodes mustEqual false
    }
        
    "toRawScala" in {
      AddOperator(Value("a"), Value("b")).toRawScala mustEqual "a + b"
    }
    
    "validate true when contains valid nodes" in {
      AddOperator(Value("a"), Value("b")).validate mustEqual true
    }
    
    "validate false when contains an empty node" in {
      AddOperator(Value("a"), Empty()).validate mustEqual false
    }
    
    "validate false when contains a node that is not valid for this level" in {
      AddOperator(Value("a"), ObjectM(Nil)).validate mustEqual false
    }
  }
}