package nodes

import org.specs2.mutable._
import nodes.helpers._
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import ai.helpers.TestAiModule
import nodes.helpers.CreateNode
import nodes.helpers.Scope
import nodes.helpers.CreateSeqNodes
import com.tzavellas.sse.guice.ScalaModule
import ai.IRandomNumberGenerator

class ObjectDefFactorySpec extends Specification with Mockito {
  "ObjectDefFactory" should {
    class TestDevModule extends ScalaModule {
      override def configure() {
        bind(classOf[AddOperatorFactory]).asEagerSingleton()
        bind(classOf[Empty]).asEagerSingleton()
        bind(classOf[FunctionMFactory]).asEagerSingleton()
        bind(classOf[NodeTreeFactory]).asEagerSingleton()
        bind(classOf[ObjectDefFactory]).asEagerSingleton()
        bind(classOf[ValueRefFactory]).asEagerSingleton()
        bind(classOf[ValDclInFunctionParamFactory]).asEagerSingleton()
        bind(classOf[Scope]).toInstance(Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1))
        bind(classOf[ICreateNode]).toInstance(CreateNode())
        bind(classOf[CreateSeqNodes]).asEagerSingleton()

        val rng = mock[IRandomNumberGenerator]
        rng.nextInt(any[Int]) returns 2
        rng.nextBoolean() returns true

        bind(classOf[IRandomNumberGenerator]).toInstance(rng)

        bind(classOf[MemoizeDi[Boolean]]).toInstance(MemoizeDi[Boolean]())
      }
    }

    val injector: Injector = Guice.createInjector(new TestDevModule, new TestAiModule)
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