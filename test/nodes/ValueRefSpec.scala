package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class ValueRefSpec extends Specification with Mockito {
  "ValueRef" should {
    "toRawScala" in {
      ValueRef("a").toRawScala mustEqual "a"
    }

    "validate" in {
      "false given it cannot terminate in under N steps" in {
        val s = Scope(maxDepth = 0)
        ValueRef("a").validate(s) mustEqual false
      }

      "true given a non-empty name" in {
        val s = Scope(maxDepth = 10)
        ValueRef("a").validate(s) mustEqual true
      }

      "false given an empty name" in {
        val s = Scope(maxDepth = 10)
        ValueRef("").validate(s) mustEqual false
      }
    }
  }
}