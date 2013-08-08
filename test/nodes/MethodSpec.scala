package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.execute.PendingUntilFixed
import org.specs2.execute.PendingUntilFixed

class MethodSpec extends Specification {
  "Method" should {
    "canTerminate in 3 steps" in {
      Method(Seq(AddOperator(Value("a"), Value("b")))).canTerminate(3) mustEqual true
    }

    "can not terminate in 2 steps" in {
      Method(Seq(AddOperator(Value("a"), Value("b")))).canTerminate(2) mustEqual false
    }

    "validate" in {
      "true given none empty" in {
        Method(Seq(AddOperator(Value("a"), Value("b")))).validate mustEqual true
      }

      "false given has an empty node" in {
        Method(Seq(AddOperator(Value("a"), Empty()))).validate mustEqual false
      }
    }

    "toRawScala" in {
      Method(Seq(AddOperator(Value("a"), Value("b")))).toRawScala mustEqual "def f1(a: Int, b: Int) = { a + b }"
    }
  }
}