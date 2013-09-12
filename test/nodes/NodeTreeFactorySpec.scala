package nodes

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.{CreateSeqNodes, CreateNode, Scope, DevModule}
import com.google.inject.Injector
import com.google.inject.Guice
import ai.helpers.TestAiModule
import scala.util.Random

class NodeTreeFactorySpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTreeFactory" should {
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
        rng.nextInt(any[Int]) returns 3

        bind(classOf[Random]).toInstance(rng)
      }
    }

    val injector: Injector = Guice.createInjector(new TestDevModule, new TestAiModule)
    val factory = injector.getInstance(classOf[NodeTreeFactory])

    "create" in {
      "returns instance of this type" in {
        val s = Scope(maxDepth = 10, maxObjectsInTree = 3)

        val instance = factory.create(s)

        instance must beAnInstanceOf[NodeTree]
      }

      "returns 3 children given scope with 3 maxFuncsInObject (and rng mocked)" in {
        val s = Scope(numFuncs = 0, maxDepth = 10, maxFuncsInObject = 3)

        val instance = factory.create(scope = s)

        instance must beLike {
          case NodeTree(child) => child.length mustEqual 3
        }
      }

      "throw if you ask updateScope" in {
        val s = mock[Scope]
        
        factory.updateScope(s) must throwA[scala.RuntimeException]
      }
    }
  }
}