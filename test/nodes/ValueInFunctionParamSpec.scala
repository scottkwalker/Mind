package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class ValueInFunctionParamSpec extends Specification with Mockito {
  "ValueInFunctionParam" should {
    "toRawScala" in {
      val p = mock[IntegerM]
      p.toRawScala returns "Int"
      val name = "a"

      ValueInFunctionParam(name, p).toRawScala mustEqual "a: Int"
    }

    "validate" in {
      "false given it cannot terminate in under N steps" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns false
        val name = "a"
        val p = mock[IntegerM]

        ValueInFunctionParam(name, p).validate(s) mustEqual false
      }

      "false given an empty name" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns true
        val name = ""
        val p = mock[IntegerM]

        ValueInFunctionParam(name, p).validate(s) mustEqual false
      }

      "false given an invalid child" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns true
        val name = "a"
        val p = mock[IntegerM]
        p.validate(any[Scope]) returns false

        ValueInFunctionParam(name, p).validate(s) mustEqual false
      }

      "true given it can terminate, has a non-empty name and valid child" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns true
        val name = "a"
        val p = mock[IntegerM]
        p.validate(any[Scope]) returns true

        ValueInFunctionParam(name, p).validate(s) mustEqual true
      }
    }

    "replaceEmpty" in {
      "calls replaceEmpty on child nodes" in {
        val s = mock[Scope]
        val name = "a"
        val p = mock[IntegerM]
        p.replaceEmpty(any[Scope]) returns p
        val instance = ValueInFunctionParam(name, p)

        instance.replaceEmpty(s)

        there was one(p).replaceEmpty(any[Scope])
      }

      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val name = "a"
        val p = mock[IntegerM]
        p.replaceEmpty(any[Scope]) returns p
        val instance = ValueInFunctionParam(name, p)

        val result = instance.replaceEmpty(s)

        result mustEqual instance
      }

      "returns without empty nodes given there were empty nodes" in {
        val s = mock[Scope]
        val name = "a"
        val p = mock[Empty]
        val instance = ValueInFunctionParam(name, p)

        val result = instance.replaceEmpty(s)

        result must beLike {
          case ValueInFunctionParam(name, primitiveType) => {
            name mustEqual "a"
            primitiveType must beAnInstanceOf[IntegerM]
          }
        }
      }
    }
  }
}