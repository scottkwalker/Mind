package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class IntegerMSpec extends Specification with Mockito {
  "IntegerM" should {
    "toRawScala" in {
      IntegerM().toRawScala mustEqual "Int"
    }

    "validate returns true" in {
      val s = mock[Scope]
      IntegerM().validate(s) mustEqual true
    }

    "replaceEmpty" in {
      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val instance = IntegerM()

        instance.replaceEmpty(s) mustEqual instance
      }
    }
  }
}