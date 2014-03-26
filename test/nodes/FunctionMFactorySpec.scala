package nodes

import org.specs2.mutable._
import org.specs2.mock.Mockito
import com.google.inject.{Injector, Guice}
import nodes.helpers.Scope
import ai.IRandomNumberGenerator
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule

class FunctionMFactorySpec extends Specification with Mockito {
  "FunctionMFactory" should {
    class TestDevModule(rng: IRandomNumberGenerator) extends DevModule(randomNumberGenerator = rng) {
      override def bindFunctionMFactory(): Unit = {
        val n: Node = mock[ValueRef]
        val f = mock[FunctionMFactory]
        f.create(any[Scope]) returns n
        bind(classOf[FunctionMFactory]).toInstance(f)
      }
    }

    val rng = mock[IRandomNumberGenerator]
    rng.nextInt(any[Int]) returns 2
    rng.nextBoolean() returns true

    val injector: Injector = Guice.createInjector(new TestDevModule(rng), new LegalGamerModule)
    val factory = injector.getInstance(classOf[FunctionMFactory])

    "create" in {
      "returns instance of this type" in {
        val s = Scope(maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[FunctionM]
      }

      "returns expected given scope with 0 functions" in {
        val s = Scope(numFuncs = 0, maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beLike {
          case FunctionM(_, _, name) => name mustEqual "f0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = Scope(numFuncs = 1, maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beLike {
          case FunctionM(_, _, name) => name mustEqual "f1"
        }
      }

      "update scope calls increment functions" in {
        val s = mock[Scope]

        factory.updateScope(s)

        there was one(s).incrementFuncs
      }

      "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
        val s = Scope(numFuncs = 0, maxDepth = 10, maxParamsInFunc = 3, maxExpressionsInFunc = 3)

        val instance = factory.create(scope = s)

        instance must beLike {
          case FunctionM(_, children, _) => children.length mustEqual 3
        }
      }
    }
  }
}