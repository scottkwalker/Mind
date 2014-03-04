package nodes

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import nodes.helpers._
import com.google.inject.{Injector, Guice}
import nodes.helpers.Scope
import scala.Some
import ai.IRandomNumberGenerator
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule

class NodeTreeFactorySpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTreeFactory" should {
    class TestDevModule(rng: IRandomNumberGenerator) extends DevModule(scope = Scope(maxDepth = 10, maxExpressionsInFunc = 2, maxFuncsInObject = 3, maxParamsInFunc = 2, maxObjectsInTree = 3),
      randomNumberGenerator = rng) {}

    val rng = mock[IRandomNumberGenerator]
    rng.nextInt(any[Int]) returns 2
    rng.nextBoolean() returns true

    val injector: Injector = Guice.createInjector(new TestDevModule(rng), new LegalGamerModule)
    val factory = injector.getInstance(classOf[NodeTreeFactory])
    val s = injector.getInstance(classOf[IScope])

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
        c.create(any[IScope]) returns n

        val instance = factory.create(scope = s, premade = Some(Seq(c)))

        instance must beLike {
          case NodeTree(child) =>
            child.length mustEqual 4 // 3 generated and 1 premade
            child(3) mustEqual n
        }
      }

      "throw if you ask updateScope" in {
        val s = mock[IScope]

        factory.updateScope(s) must throwA[scala.RuntimeException]
      }
    }
  }
}