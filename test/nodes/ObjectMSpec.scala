package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class ObjectMSpec extends Specification {
  "ObjectM" should {
    "validate" in {
      "true given it can terminates in under N steps" in {
        val objectM = ObjectM(Seq(Method(Seq(AddOperator(ValueM("a"), ValueM("b"))))))

        objectM.validate(4) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        val objectM = ObjectM(Seq(Method(Seq(AddOperator(ValueM("a"), ValueM("b"))))))

        objectM.validate(3) mustEqual false
      }

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
    
    "create returns instance of this type" in {
      ObjectM.create must beAnInstanceOf[ObjectM]
    }
  }
}