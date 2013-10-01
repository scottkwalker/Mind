package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class ValueRefSpec extends Specification with Mockito {
  "ValueRef" should {
    "toRawScala" in {
      val name = "a"

      ValueRef(name).toRawScala mustEqual name
    }

    "validate" in {
      "false given it cannot terminate in under N steps" in {
        val s = Scope(maxDepth = 0)
        val name = "a"

        ValueRef(name).validate(s) mustEqual false
      }

      "true given a non-empty name" in {
        val s = Scope(maxDepth = 10)
        val name = "a"

        ValueRef(name).validate(s) mustEqual true
      }

      "false given an empty name" in {
        val s = Scope(maxDepth = 10)
        val name = ""

        ValueRef(name).validate(s) mustEqual false
      }
    }

    "replaceEmpty" in {
      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val name = "a"
        val instance = ValueRef(name)

        instance.replaceEmpty(s) mustEqual instance
      }
    }
  }
}