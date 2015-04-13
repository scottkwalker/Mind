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
import models.domain.Step
import models.domain.scala.ObjectImpl
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class ObjectFactorySpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "return an instance of the expected type" in {
      val (objectFactory, _, scope) = build()

      val step = objectFactory.fillEmptySteps(scope = scope)

      whenReady(step) {
        _ mustBe an[ObjectImpl]
      }(config = patienceConfig)
    }

    "returns expected if scope has 0 existing objects" in {
      val (objectFactory, _, scope) = build()

      val step = objectFactory.fillEmptySteps(scope = scope)

      whenReady(step) {
        case ObjectImpl(_, name) => name must equal("o0")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "returns expected if scope has 1 existing object" in {
      val (objectFactory, _, scope) = build()
      when(scope.numObjects).thenReturn(1)

      val step = objectFactory.fillEmptySteps(scope = scope)

      whenReady(step) {
        case ObjectImpl(_, name) => name must equal("o1")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "call CreateSeqNodes.create once" in {
      val (objectFactory, createSeqNodes, scope) = build()

      val step = objectFactory.fillEmptySteps(scope = scope)

      whenReady(step) { _ =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
        verifyNoMoreInteractions(createSeqNodes)
      }(config = patienceConfig)
    }
  }

  "updateScope" must {
    "call increment objects" in {
      val (objectFactory, _, scope) = build()

      objectFactory.updateScope(scope)

      verify(scope, times(1)).incrementObjects
      verifyNoMoreInteractions(scope)
    }
  }

  "createNodes" must {
    "call CreateSeqNodes.create once" in {
      val (objectFactory, createSeqNodes, scope) = build()

      val step = objectFactory.createNodes(scope = scope)

      whenReady(step) { _ =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
        verifyNoMoreInteractions(createSeqNodes)
      }(config = patienceConfig)
    }
  }

  "createParams" must {
    "throw an exception" in {
      val (objectFactory, _, scope) = build()

      a[RuntimeException] must be thrownBy objectFactory.createParams(scope).futureValue
    }
  }

  private def build(nextInt: Int = 0) = {
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
    (injector.getInstance(classOf[ObjectFactory]), createSeqNodes.stub, scope)
  }
}