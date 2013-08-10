package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class ObjectMSpec extends Specification {
  "ObjectM" should {
    "canTerminate in 4 steps" in {
      val objectM = ObjectM(Seq(Method(Seq(AddOperator(ValueM("a"), ValueM("b"))))))

      objectM.canTerminate(4) mustEqual true
    }

    "can not terminate in 3 steps" in {
      val objectM = ObjectM(Seq(Method(Seq(AddOperator(ValueM("a"), ValueM("b"))))))

      objectM.canTerminate(3) mustEqual false
    }

    "validate" in {
      "true given no empty nodes" in {
        val objectM = ObjectM(Seq(Method(Seq(AddOperator(ValueM("a"), ValueM("b"))))))

        objectM.validate(10) mustEqual true
      }

      "false given single empty method node" in {
        val objectM = ObjectM(Seq(Empty()))
        objectM.validate(10) mustEqual false
      }

      "false given empty method node in a sequence" in {
        val objectM = ObjectM(Seq(Method(Seq(AddOperator(ValueM("a"), ValueM("b")))), Empty()))
        objectM.validate(10) mustEqual false
      }
    }

    "toRawScala" in {
      val objectM = ObjectM(Seq(Method(Seq(AddOperator(ValueM("a"), ValueM("b"))))))

      objectM.toRawScala mustEqual "object Individual { def f1(a: Int, b: Int) = { a + b } }"
    }
  }
}