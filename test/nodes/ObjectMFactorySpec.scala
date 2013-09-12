package nodes

import org.specs2.mutable._
import nodes.helpers.{CreateSeqNodes, CreateNode, Scope, DevModule}
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import ai.helpers.TestAiModule
import scala.util.Random

class ObjectMFactorySpec extends Specification with Mockito {
  "ObjectMFactory" should {
    class TestDevModule extends DevModule {
      override def configure() {
        bind(classOf[AddOperatorFactory]).asEagerSingleton()
        bind(classOf[Empty]).asEagerSingleton()
        bind(classOf[FunctionMFactory]).asEagerSingleton()
        bind(classOf[NodeTreeFactory]).asEagerSingleton()
        bind(classOf[ObjectMFactory]).asEagerSingleton()
        bind(classOf[ValueRefFactory]).asEagerSingleton()
        bind(classOf[ValueInFunctionParamFactory]).asEagerSingleton()
        bind(classOf[Scope]).toInstance(Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1))
        bind(classOf[CreateNode]).asEagerSingleton()
        bind(classOf[CreateSeqNodes]).asEagerSingleton()

        val rng = mock[Random]
        rng.nextInt(any[Int]) returns 2

        bind(classOf[Random]).toInstance(rng)
      }
    }

    val injector: Injector = Guice.createInjector(new TestDevModule, new TestAiModule)
    val factory = injector.getInstance(classOf[ObjectMFactory])

    "create" in {
      "returns instance of this type" in {
        val s = Scope(maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[ObjectM]
      }

      "returns expected given scope with 0 functions" in {
        val s = Scope(numObjects = 0, maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ObjectM(_, name) => name mustEqual "o0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = Scope(numObjects = 1, maxDepth = 10)

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

      "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
        val s = Scope(numFuncs = 0, maxDepth = 10, maxFuncsInObject = 3)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ObjectM(child, _) => child.length mustEqual 3
        }
      }
    }
  }
}