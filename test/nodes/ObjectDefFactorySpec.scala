package nodes

import org.specs2.mutable._
import org.specs2.mock.Mockito
import com.google.inject.{Injector, Guice}
import nodes.helpers.Scope
import ai.IRandomNumberGenerator
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule

class ObjectDefFactorySpec extends Specification with Mockito {
  "ObjectDefFactory" should {
    val rng = mock[IRandomNumberGenerator]
    rng.nextInt(any[Int]) returns 2
    rng.nextBoolean() returns true

    val injector: Injector = Guice.createInjector(new DevModule(randomNumberGenerator = rng), new LegalGamerModule)
    val factory = injector.getInstance(classOf[ObjectDefFactory])

    "create" in {
      "returns instance of this type" in {
        val s = Scope(maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[ObjectDef]
      }

      "returns expected given scope with 0 functions" in {
        val s = Scope(numObjects = 0, maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ObjectDef(_, name) => name mustEqual "o0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = Scope(numObjects = 1, maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ObjectDef(_, name) => name mustEqual "o1"
        }
      }

      "update scope calls increment objects" in {
        val s = mock[Scope]

        factory.updateScope(s)

        there was one(s).incrementObjects
      }

      "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
        val s = Scope(numFuncs = 0, maxDepth = 10, maxFuncsInObject = 3)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ObjectDef(child, _) => child.length mustEqual 3
        }
      }
    }
  }
}