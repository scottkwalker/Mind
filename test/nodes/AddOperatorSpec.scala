package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import nodes.helpers.Scope

class AddOperatorSpec extends Specification {
  "AddOperator" should {
    "toRawScala" in {
      AddOperator(ValueRef("a"), ValueRef("b")).toRawScala mustEqual "a + b"
    }

    "validate" in {
      "true given it can terminates in under N steps" in {
        AddOperator(ValueRef("a"), ValueRef("b")).validate(2) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        AddOperator(ValueRef("a"), ValueRef("b")).validate(1) mustEqual false
      }

      "true given none empty" in {
        AddOperator(ValueRef("a"), ValueRef("b")).validate(10) mustEqual true
      }

      "false given contains an empty node" in {
        AddOperator(ValueRef("a"), Empty()).validate(10) mustEqual false
      }

      "false given contains a node that is not valid for this level" in {
        AddOperator(ValueRef("a"), ObjectM(Nil)).validate(10) mustEqual false
      }
    }
    
    "create returns instance of this type" in {
      AddOperator.create(scope = Scope()) must beAnInstanceOf[AddOperator]
    }
  }
}