package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class AddOperatorSpec extends Specification with Mockito {
  "AddOperator" should {
    "toRawScala" in {
      val a = mock[ValueRef]
      a.toRawScala returns "a"
      val b = mock[ValueRef]
      b.toRawScala returns "b"

      AddOperator(a, b).toRawScala mustEqual "a + b"
    }

    "validate" in {
      "true given child nodes can terminate in under N steps" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true
        
        AddOperator(v, v).validate(2) mustEqual true
      }
      
      "false given it cannot terminate in 0 steps" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns false
        
        AddOperator(v, v).validate(0) mustEqual false
      }

      "false given child nodes cannot terminate in under N steps" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns false
        
        AddOperator(v, v).validate(10) mustEqual false
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