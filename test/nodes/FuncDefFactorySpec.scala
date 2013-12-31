package nodes

import org.specs2.mutable._
import nodes.helpers._
import org.specs2.mock.Mockito
import com.google.inject.{Injector, Guice}
import ai.helpers.TestAiModule
import nodes.helpers.Scope
import ai.IRandomNumberGenerator

class FuncDefFactorySpec extends Specification with Mockito {
  "FuncDefFactory" should {
    class TestDevModule(rng: IRandomNumberGenerator) extends DevModule(randomNumberGenerator = rng) {
      override def bindAddOperatorFactory = {
        val n: Node = mock[ValueRef]
        val f = mock[AddOperatorFactory]
        f.create(any[Scope]) returns n
        bind(classOf[AddOperatorFactory]).toInstance(f)
      }
    }

    val rng = mock[IRandomNumberGenerator]
    rng.nextInt(any[Int]) returns 2
    rng.nextBoolean() returns true

    val injector: Injector = Guice.createInjector(new TestDevModule(rng), new TestAiModule)
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