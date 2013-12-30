package nodes

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Guice
import ai.helpers.TestAiModule
import nodes.helpers.CreateNode
import nodes.helpers.Scope
import nodes.helpers.CreateSeqNodes
import scala.Some
import com.tzavellas.sse.guice.ScalaModule
import ai.IRandomNumberGenerator

class NodeTreeFactorySpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTreeFactory" should {
    class TestDevModule extends ScalaModule {
      override def configure() {
        bind(classOf[AddOperatorFactory]).asEagerSingleton()
        bind(classOf[Empty]).asEagerSingleton()
        bind(classOf[FunctionMFactory]).asEagerSingleton()
        bind(classOf[NodeTreeFactory]).asEagerSingleton()
        bind(classOf[ObjectDefFactory]).asEagerSingleton()
        bind(classOf[ValueRefFactory]).asEagerSingleton()
        bind(classOf[ValDclInFunctionParamFactory]).asEagerSingleton()
        bind(classOf[Scope]).toInstance(Scope(maxDepth = 10, maxExpressionsInFunc = 2, maxFuncsInObject = 3, maxParamsInFunc = 2, maxObjectsInTree = 3))
        bind(classOf[ICreateNode]).toInstance(CreateNode())
        bind(classOf[CreateSeqNodes]).asEagerSingleton()

        val rng = mock[IRandomNumberGenerator]
        rng.nextInt(any[Int]) returns 2
        rng.nextBoolean() returns true

        bind(classOf[IRandomNumberGenerator]).toInstance(rng)

        bind(classOf[MemoizeDi[IScope, Boolean]]).toInstance(MemoizeDi[IScope, Boolean]())
      }
    }

    val injector: Injector = Guice.createInjector(new TestDevModule, new TestAiModule)
    val factory = injector.getInstance(classOf[NodeTreeFactory])
    val s = injector.getInstance(classOf[Scope])

    "create" in {
      "returns instance of this type" in {
        val instance = factory.create(s)

        instance must beAnInstanceOf[NodeTree]
      }

      "returns 3 children given scope with 3 maxFuncsInObject (and rng mocked)" in {
        val instance = factory.create(scope = s)

        instance must beLike {
          case NodeTree(child) => child.length mustEqual 3
        }
      }

      "returns 4 children given 1 premade and scope with 3 maxFuncsInObject (and rng mocked)" in {
        val n = mock[Node]
        val c = mock[ICreateChildNodes]
        c.create(any[Scope]) returns n

        val instance = factory.create(scope = s, premade = Some(Seq(c)))

        instance must beLike {
          case NodeTree(child) => {
            child.length mustEqual 4 // 3 generated and 1 premade
            child(3) mustEqual n
          }
        }
      }

      "throw if you ask updateScope" in {
        val s = mock[Scope]

        factory.updateScope(s) must throwA[scala.RuntimeException]
      }
    }
  }
}