package nodes

import ai.IRandomNumberGenerator
import com.google.inject.{Guice, Injector}
import models.domain.common.Node
import models.domain.scala.NodeTree
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import nodes.helpers.{Scope, _}
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.helpers.UnitSpec

final class NodeTreeFactorySpec extends UnitSpec {

  "create" should {
    "returns instance of this type" in {
      val instance = factory.create(s)

      instance shouldBe a[NodeTree]
    }

    "returns 3 children given scope with 3 maxFuncsInObject (and rng mocked)" in {
      val instance = factory.create(scope = s)

      instance match {
        case NodeTree(child) => child.length should equal(3)
        case _ => fail("wrong type")
      }
    }

    "returns 4 children given 1 premade and scope with 3 maxFuncsInObject (and rng mocked)" in {
      val n = mock[Node]
      val c = mock[ICreateChildNodes]
      when(c.create(any[IScope])).thenReturn(n)

      val instance = factory.create(scope = s, premadeChildren = Seq(c))

      instance match {
        case NodeTree(child) =>
          child.length should equal(4) // 3 generated and 1 premade
          child(3) should equal(n)
        case _ => fail("wrong type")
      }
    }

    "throw if you ask updateScope" in {
      val s = mock[IScope]

      a[RuntimeException] should be thrownBy factory.updateScope(s)
    }
  }

  private val rng = mock[IRandomNumberGenerator]
  private val injector: Injector = Guice.createInjector(new TestDevModule(rng), new LegalGamerModule)
  when(rng.nextInt(any[Int])).thenReturn(2)
  when(rng.nextBoolean()).thenReturn(true)
  private val factory = injector.getInstance(classOf[NodeTreeFactoryImpl])
  private val s = injector.getInstance(classOf[IScope])

  final class TestDevModule(rng: IRandomNumberGenerator) extends DevModule(scope = Scope(height = 10, maxExpressionsInFunc = 2, maxFuncsInObject = 3, maxParamsInFunc = 2, maxObjectsInTree = 3),
    randomNumberGenerator = rng) {}
}