package replaceEmpty

import ai.RandomNumberGenerator
import composition.{StubIScope, StubRng, TestComposition}
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.TypeTree
import org.mockito.Matchers._
import org.mockito.Mockito._
import scala.concurrent.Future

final class TypeTreeFactorySpec extends TestComposition {

  "create" must {
    "returns instance of this type" in {
      val (typeTreeFactory, scope) = build()

      val result = typeTreeFactory.create(scope)

      whenReady(result) { result =>
        result mustBe a[TypeTree]
      }(config = whenReadyPatienceConfig)
    }

    "returns 3 children given scope with 3 maxFuncsInObject (and rng mocked)" in {
      val (typeTreeFactory, scope) = build(nextInt = 3)
      val result = typeTreeFactory.create(scope = scope)

      whenReady(result) {
        case TypeTree(child) => child.length must equal(3)
        case _ => fail("wrong type")
      }(config = whenReadyPatienceConfig)
    }

    "returns 4 children given 1 premade and scope with 3 maxFuncsInObject (and rng mocked)" in {
      val premadeNode = mock[Instruction]
      val premadeChildren = {
        val replaceEmpty = mock[ReplaceEmpty]
        when(replaceEmpty.create(any[IScope])).thenReturn(Future.successful(premadeNode))
        Seq(replaceEmpty)
      }
      val (typeTreeFactory, scope) = build(nextInt = 3)

      val result = typeTreeFactory.create(scope = scope, premadeChildren = premadeChildren)

      whenReady(result) {
        case TypeTree(child) =>
          child.length must equal(4) // 3 generated and 1 premade
          child.last must equal(premadeNode) // The premade are concatenated to the end of the seq
        case _ => fail("wrong type")
      }(config = whenReadyPatienceConfig)
    }

    "throw if you ask updateScope" in {
      val scope = mock[IScope]
      val (typeTreeFactory, _) = build()
      a[RuntimeException] must be thrownBy typeTreeFactory.updateScope(scope)
    }
  }

  private def build(nextInt: Int = 0) = {
    val rng: RandomNumberGenerator = mock[RandomNumberGenerator]
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val injector = testInjector(new StubRng(randomNumberGenerator = rng), new StubIScope)
    (injector.getInstance(classOf[TypeTreeFactory]), injector.getInstance(classOf[IScope]))
  }
}