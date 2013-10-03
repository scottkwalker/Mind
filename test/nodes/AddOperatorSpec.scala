package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector

class AddOperatorSpec extends Specification with Mockito {
  "AddOperator" should {
    "toRawScala" in {
      val a = mock[ValueRef]
      a.toRawScala returns "STUB_A"
      val b = mock[ValueRef]
      b.toRawScala returns "STUB_B"

      AddOperator(a, b).toRawScala mustEqual "STUB_A + STUB_B"
    }

    "validate" in {
      "true given child nodes can terminate in under N steps" in {
        val s = Scope(maxDepth = 2)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true
        
        AddOperator(v, v).validate(s) mustEqual true
      }
      
      "false given it cannot terminate in 0 steps" in {
        val s = Scope(maxDepth = 0)
        val v = mock[ValueRef]
        v.validate(any[Scope]) throws new RuntimeException
        
        AddOperator(v, v).validate(s) mustEqual false
      }

      "false given child nodes cannot terminate in under N steps" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns false
        
        AddOperator(v, v).validate(s) mustEqual false
      }

      "true given none empty" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        AddOperator(v, v).validate(s) mustEqual true
      }

      "false given contains an empty node" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        AddOperator(v, Empty()).validate(s) mustEqual false
      }

      "false given contains a node that is not valid for this level" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        AddOperator(v, ObjectM(Nil, "ObjectM0")).validate(s) mustEqual false
      }
    }

    "replaceEmpty" in {
      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val v = mock[ValueRef]
        val i = mock[Injector]

        val instance = AddOperator(v, v)

        instance.replaceEmpty(s, i) mustEqual instance
      }
    }
  }
}