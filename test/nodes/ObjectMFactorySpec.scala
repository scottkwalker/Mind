package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class ObjectMFactorySpec extends Specification with Mockito {
  "ObjectMFactory" should {
    "create" in {
      "returns instance of this type" in {
        val s = mock[Scope]
        s.numFuncs returns 0
        s.numObjects returns 0
        s.numVals returns 0
        
        ObjectMFactory().create(scope = s) must beAnInstanceOf[ObjectM]
      }
      
      "returns expected given scope with 0 functions" in {
        val s = mock[Scope]
        s.numObjects returns 0

        ObjectMFactory().create(scope = s) must beLike {
          case ObjectM(_, name) => name mustEqual "o0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = mock[Scope]
        s.numObjects returns 1

        ObjectMFactory().create(scope = s) must beLike {
          case ObjectM(_, name) => name mustEqual "o1"
        }
      }
    }
          
  }
}