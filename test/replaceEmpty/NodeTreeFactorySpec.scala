package replaceEmpty

import composition.{StubIScope, StubRng, TestComposition}
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.NodeTree
import org.mockito.Matchers._
import org.mockito.Mockito._
import scala.concurrent.Future

final class NodeTreeFactorySpec extends TestComposition {

  "create" must {
    "returns instance of this type" in {
      val instance = factory.create(scope)

      whenReady(instance, browserTimeout) { result =>
        result mustBe a[NodeTree]
      }
    }

    "returns 3 children given scope with 3 maxFuncsInObject (and rng mocked)" in {
      val instance = factory.create(scope = scope)

      whenReady(instance, browserTimeout) {
        case NodeTree(child) => child.length must equal(3)
        case _ => fail("wrong type")
      }
    }

    "returns 4 children given 1 premade and scope with 3 maxFuncsInObject (and rng mocked)" in {
      val n = mock[Instruction]
      val c = mock[ReplaceEmpty]
      when(c.create(any[IScope])).thenReturn(Future.successful(n))

      val instance = factory.create(scope = scope, premadeChildren = Seq(c))

      whenReady(instance, browserTimeout) {
        case NodeTree(child) =>
          child.length must equal(4) // 3 generated and 1 premade
          child(3) must equal(n)
        case _ => fail("wrong type")
      }
    }

    "throw if you ask updateScope" in {
      val s = mock[IScope]
      a[RuntimeException] must be thrownBy factory.updateScope(s)
    }
  }

  private val injector = testInjector(new StubRng, new StubIScope)
  private val factory = injector.getInstance(classOf[NodeTreeFactoryImpl])
  private val scope = injector.getInstance(classOf[IScope])
}