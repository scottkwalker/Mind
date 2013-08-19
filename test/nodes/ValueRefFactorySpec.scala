package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule

class ValueRefFactorySpec extends Specification with Mockito {
  "ValueRefFactorySpec" should {
    val injector: Injector = Guice.createInjector(new DevModule)
    val factory = injector.getInstance(classOf[ValueRefFactory])
    
    "create" in {
      "returns instance of this type" in {
        val s = mock[Scope]
        s.numVals returns 0

        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[ValueRef]
      }

      "returns expected given scope with 0 vals" in {
        val s = mock[Scope]
        s.numVals returns 0

        val instance = factory.create(scope = s)

        instance must beLike {
          case ValueRef(name) => name mustEqual "v0"
        }
      }

      "returns expected given scope with 1 val" in {
        val s = mock[Scope]
        s.numVals returns 1

        val instance = factory.create(scope = s)

        instance must beLike {
          case ValueRef(name) => name mustEqual "v1"
        }
      }
    }
    
    "has no possible children" in {
      factory.allPossibleChildren.length mustEqual 0
    }
  }
}