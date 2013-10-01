package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class IntegerSpec extends Specification with Mockito {
  "Integer" should {
    "toRawScala" in {
      IntegerM().toRawScala mustEqual "Int"
    }

    "validate returns true" in {
      val s = mock[Scope]
      IntegerM().validate(s) mustEqual true
    }

    "replaceEmpty" in {
      "returns same when no wildcards" in {
        val s = mock[Scope]
        val instance = IntegerM()

        instance.replaceEmpty(s) mustEqual instance
      }
    }
  }
}