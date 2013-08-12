package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.execute.PendingUntilFixed
import org.specs2.execute.PendingUntilFixed

class MethodSpec extends Specification {
  "Method" should {
    "validate" in {
      "true given it can terminates in under N steps" in {
        Method(Seq(AddOperator(ValueM("a"), ValueM("b")))).validate(3) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        Method(Seq(AddOperator(ValueM("a"), ValueM("b")))).validate(2) mustEqual false
      }

      "true given none empty" in {
        Method(Seq(AddOperator(ValueM("a"), ValueM("b")))).validate(10) mustEqual true
      }

      "false given has an empty node" in {
        Method(Seq(AddOperator(ValueM("a"), Empty()))).validate(10) mustEqual false
      }
    }

    "toRawScala" in {
      Method(Seq(AddOperator(ValueM("a"), ValueM("b")))).toRawScala mustEqual "def f1(a: Int, b: Int) = { a + b }"
    }
  }
}