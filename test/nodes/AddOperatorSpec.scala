package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class AddOperatorSpec extends Specification {
  "AddOperator" should {
    "canTerminate in 2 steps" in {
      AddOperator(ValueM("a"), ValueM("b")).canTerminate(2) mustEqual true
    }

    "can not terminate in 1 steps" in {
      AddOperator(ValueM("a"), ValueM("b")).canTerminate(1) mustEqual false
    }

    "toRawScala" in {
      AddOperator(ValueM("a"), ValueM("b")).toRawScala mustEqual "a + b"
    }

    "validate" in {
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
  }
}