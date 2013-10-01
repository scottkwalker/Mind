package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule
import ai.helpers.TestAiModule

class ValueInFunctionParamFactorySpec extends Specification with Mockito {
  "ValueInFunctionParamFactorySpec" should {
    val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
    val factory = injector.getInstance(classOf[ValueInFunctionParamFactory])

    "create" in {
      "returns instance of this type" in {
        val s = mock[Scope]
        s.numVals returns 0

        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[ValueInFunctionParam]
      }

      "returns expected given scope with 0 vals" in {
        val s = mock[Scope]
        s.numVals returns 0

        val instance = factory.create(scope = s)

        instance must beLike {
          case ValueInFunctionParam(name, primitiveType) => {name mustEqual "v0"
            primitiveType must beAnInstanceOf[IntegerM]
          }
        }
      }

      "returns expected given scope with 1 val" in {
        val s = mock[Scope]
        s.numVals returns 1

        val instance = factory.create(scope = s)

        instance must beLike {
          case ValueInFunctionParam(name, primitiveType) => {name mustEqual "v1"
            primitiveType must beAnInstanceOf[IntegerM]
          }
        }
      }
    }

    "has no possible children" in {
      factory.neighbours.length mustEqual 0
    }

    "updateScope increments vals" in {
      val s = mock[Scope]

      factory.updateScope(s)

      there was one(s).incrementVals
    }
  }
}