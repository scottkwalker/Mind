package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class AddOperatorSpec extends Specification {
  "AddOperator" should {
    "toRawScala" in {
      AddOperator(ValueM("a"), ValueM("b")).toRawScala mustEqual "a + b"
    }

    "validate" in {
      "true given it can terminates in under N steps" in {
        AddOperator(ValueM("a"), ValueM("b")).validate(2) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        AddOperator(ValueM("a"), ValueM("b")).validate(1) mustEqual false
      }

      "true given none empty" in {
        AddOperator(ValueM("a"), ValueM("b")).validate(10) mustEqual true
      }

      "false given contains an empty node" in {
        AddOperator(ValueM("a"), Empty()).validate(10) mustEqual false
      }

      "false given contains a node that is not valid for this level" in {
        AddOperator(ValueM("a"), ObjectM(Nil)).validate(10) mustEqual false
      }
    }
    
    "create returns instance of this type" in {
      AddOperator.create must beAnInstanceOf[AddOperator]
    }
  }
}