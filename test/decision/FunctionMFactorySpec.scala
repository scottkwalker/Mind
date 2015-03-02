package decision

import composition.DecisionBindings
import composition.StubCreateNodeBinding
import composition.StubCreateSeqNodesBinding
import composition.StubLookupChildrenWithFutures
import composition.StubRngBinding
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.common.Scope
import models.domain.Step
import models.domain.scala.FunctionMImpl
import models.domain.scala.FunctionMImpl$
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class FunctionMFactorySpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "return instance of this type" in {
      val (factory, _, scope) = functionMFactory()

      val step = factory.createStep(scope = scope)

      whenReady(step) {
        _ mustBe a[FunctionMImpl]
      }(config = patienceConfig)
    }

    "return expected given scope with 0 functions" in {
      val (factory, _, scope) = functionMFactory()

      val step = factory.createStep(scope = scope)

      whenReady(step) {
        case FunctionMImpl(_, _, name) => name must equal("f0")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "return expected given scope with 1 functions" in {
      val (factory, _, scope) = functionMFactory()
      when(scope.numFuncs).thenReturn(1)

      val step = factory.createStep(scope = scope)

      whenReady(step) {
        case FunctionMImpl(_, _, name) => name must equal("f1")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "calls CreateSeqNodes.create twice (once for params and once for nodes)" in {
      val (factory, createSeqNodes, scope) = functionMFactory()

      val step = factory.createStep(scope = scope)

      whenReady(step) { _ =>
        verify(createSeqNodes, times(2)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
      }(config = patienceConfig)
    }
  }

  "createNodes" must {
    "call CreateSeqNodes.create once" in {
      val (factory, createSeqNodes, scope) = functionMFactory()

      val step = factory.createNodes(scope = scope)

      whenReady(step) { _ =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
      }(config = patienceConfig)
    }
  }

  "createParams" must {
    "call CreateSeqNodes.create once" in {
      val (factory, createSeqNodes, scope) = functionMFactory()

      val step = factory.createParams(scope = scope)

      whenReady(step) { _ =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
      }(config = patienceConfig)
    }
  }

  "updateScope" must {
    "call increment functions" in {
      val (factory, _, scope) = functionMFactory()

      factory.updateScope(scope = scope)

      verify(scope, times(1)).incrementFuncs
    }
  }

  private def functionMFactory(nextInt: Int = 0) = {
    val randomNumberGenerator = new StubRngBinding(nextInt = nextInt)
    val createSeqNodes = new StubCreateSeqNodesBinding
    val scope = mock[IScope]
    val injector = testInjector(
      randomNumberGenerator,
      createSeqNodes,
      new DecisionBindings,
      new StubLookupChildrenWithFutures,
      new StubCreateNodeBinding,
      new StubSelectionStrategyBinding
    )
    (injector.getInstance(classOf[FunctionMFactory]), createSeqNodes.stub, scope)
  }
}