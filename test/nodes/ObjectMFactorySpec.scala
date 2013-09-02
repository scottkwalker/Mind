package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule
import ai.aco.AcoModule
import ai.helpers.TestAiModule

class ObjectMFactorySpec extends Specification with Mockito {
  "ObjectMFactory" should {
    val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
    val factory = injector.getInstance(classOf[ObjectMFactory])

    "create" in {
      "returns instance of this type" in {
        val s = Scope(stepsRemaining = 10)

        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[ObjectM]
      }

      "returns expected given scope with 0 functions" in {
        val s = Scope(numObjects = 0, stepsRemaining = 10)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ObjectM(_, name) => name mustEqual "o0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = Scope(numObjects = 1, stepsRemaining = 10)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ObjectM(_, name) => name mustEqual "o1"
        }
      }

      "update scope calls increment objects" in {
        val s = mock[Scope]

        val instance = factory.updateScope(s)

        there was one(s).incrementObjects
      }

      "returns 1 child given scope with 1 maxFuncs" in {
        val s = Scope(numFuncs = 0, stepsRemaining = 10, maxFuncsInObject = 1)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ObjectM(child, _) => child.length mustEqual 1
        }
      }

      "returns 3 children given scope with 3 maxFuncs" in {
        val s = Scope(numFuncs = 0, stepsRemaining = 10, maxFuncsInObject = 3)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ObjectM(child, _) => child.length mustEqual 3
        }
      }
    }
  }
}