package nodes

import org.specs2.mutable._
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class FunctionMFactorySpec extends Specification with Mockito {
  "FunctionMFactory" should {
    "create" in {
      "returns instance of this type" in {
        val s = mock[Scope]
        s.numFuncs returns 0

        FunctionMFactory().create(scope = s) must beAnInstanceOf[FunctionM]
      }

      "returns expected given scope with 0 functions" in {
        val s = mock[Scope]
        s.numFuncs returns 0

        FunctionMFactory().create(scope = s) must beLike {
          case FunctionM(_, name) => name mustEqual "f0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = mock[Scope]
        s.numFuncs returns 1

        FunctionMFactory().create(scope = s) must beLike {
          case FunctionM(_, name) => name mustEqual "f1"
        }
      }
    }
  }
}