package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class AddOperatorSpec extends Specification with Mockito {
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
        val v = mock[ValueRef]
        v.validate(anyInt) returns true
        
        AddOperator(v, v).validate(10) mustEqual true
      }

      "false given contains an empty node" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true
        
        AddOperator(v, Empty()).validate(10) mustEqual false
      }

      "false given contains a node that is not valid for this level" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true
        
        AddOperator(v, ObjectM(Nil)).validate(10) mustEqual false
      }
    }
    
    "create returns instance of this type" in {
      val s = mock[Scope]
      s.numVals returns 0
      
      AddOperator.create(scope = s) must beAnInstanceOf[AddOperator]
    }
  }
}