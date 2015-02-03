package replaceEmpty

import ai.RandomNumberGenerator
import composition.{StubCreateSeqNodesBinding, StubIScope, StubRng, TestComposition}
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.FunctionM
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class FunctionMFactorySpec extends TestComposition {

  "create" must {
    "return instance of this type" in {
      val (factory, scope, _) = functionMFactory()

      val result = factory.create(scope = scope)

      whenReady(result) { result =>
        result mustBe a[FunctionM]
      }(config = patienceConfig)
    }

    "return expected given scope with 0 functions" in {
      val (factory, scope, _) = functionMFactory()

      val result = factory.create(scope = scope)

      whenReady(result) {
        case FunctionM(_, _, name) => name must equal("f0")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "return expected given scope with 1 functions" in {
      val (factory, scope, _) = functionMFactory(numFuncs = 1)

      val result = factory.create(scope = scope)

      whenReady(result) {
        case FunctionM(_, _, name) => name must equal("f1")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "calls CreateSeqNodes.create twice (once for params and once for nodes)" in {
      val (factory, scope, createSeqNodes) = functionMFactory()

      val result = factory.create(scope = scope)

      whenReady(result) { r =>
        verify(createSeqNodes, times(2)).create(any[Future[Set[ReplaceEmpty]]], any[IScope], any[Seq[Instruction]], any[Int])
      }(config = patienceConfig)
    }
  }

  "updateScope" must {
    "call increment functions" in {
      val scope = mock[IScope]
      val (factory, _, _) = functionMFactory()

      factory.updateScope(scope = scope)

      verify(scope, times(1)).incrementFuncs
    }
  }

  private def functionMFactory(nextInt: Int = 0, numFuncs: Int = 0) = {
    val createSeqNodes = new StubCreateSeqNodesBinding
    val rng: RandomNumberGenerator = mock[RandomNumberGenerator]
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val injector = testInjector(
      new StubRng(randomNumberGenerator = rng),
      new StubIScope(numFuncs = numFuncs),
      createSeqNodes
    )
    (injector.getInstance(classOf[FunctionMFactoryImpl]), injector.getInstance(classOf[IScope]), createSeqNodes.stub)
  }
}