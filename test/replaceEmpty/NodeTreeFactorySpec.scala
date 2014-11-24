package replaceEmpty

import ai.RandomNumberGenerator
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
      val (instance, scope) = nodeTreeFactory()

      val result = instance.create(scope)

      whenReady(result, browserTimeout) { result =>
        result mustBe a[NodeTree]
      }
    }

    "returns 3 children given scope with 3 maxFuncsInObject (and rng mocked)" in {
      val (instance, scope) = nodeTreeFactory(nextInt = 3)
      val result = instance.create(scope = scope)

      whenReady(result, browserTimeout) {
        case NodeTree(child) => child.length must equal(3)
        case _ => fail("wrong type")
      }
    }

    "returns 4 children given 1 premade and scope with 3 maxFuncsInObject (and rng mocked)" in {
      val n = mock[Instruction]
      val c = mock[ReplaceEmpty]
      when(c.create(any[IScope])).thenReturn(Future.successful(n))

      val (instance, scope) = nodeTreeFactory(nextInt = 3)
      val result = instance.create(scope = scope, premadeChildren = Seq(c))

      whenReady(result, browserTimeout) {
        case NodeTree(child) =>
          child.length must equal(4) // 3 generated and 1 premade
          child(3) must equal(n)
        case _ => fail("wrong type")
      }
    }

    "throw if you ask updateScope" in {
      val s = mock[IScope]
      val (instance, scope) = nodeTreeFactory()
      a[RuntimeException] must be thrownBy instance.updateScope(s)
    }
  }

  private def nodeTreeFactory(nextInt: Int = 0) = {
    val rng: RandomNumberGenerator = mock[RandomNumberGenerator]
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val ioc = testInjector(new StubRng(rng = rng), new StubIScope)
    (ioc.getInstance(classOf[NodeTreeFactory]), ioc.getInstance(classOf[IScope]))
  }
}