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
import models.domain.scala.Object
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Await
import scala.concurrent.Future

final class ObjectFactorySpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "returns instance of this type" in {
      val (objectFactory, _) = build()

      val step = objectFactory.createStep(scope = Scope())

      whenReady(step) {
        _ mustBe an[Object]
      }(config = patienceConfig)
    }

    "returns expected given scope with 0 existing objects" in {
      val (objectFactory, _) = build()

      val step = objectFactory.createStep(scope = Scope())

      whenReady(step) {
        case Object(_, name) => name must equal("o0")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "returns expected given scope with 1 existing objects" in {
      val (objectFactory, _) = build()

      val step = objectFactory.createStep(scope = Scope(numObjects = 1))

      whenReady(step) {
        case Object(_, name) => name must equal("o1")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "call CreateSeqNodes.create once" in {
      val (objectFactory, createSeqNodes) = build()

      val step = objectFactory.createStep(scope = Scope())

      whenReady(step) { _ =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
      }(config = patienceConfig)
    }
  }

  "updateScope" must {
    "call increment objects" in {
      val scope = mock[IScope]
      val (objectFactory, _) = build()

      objectFactory.updateScope(scope)

      verify(scope, times(1)).incrementObjects
    }
  }

  "createNodes" must {
    "call CreateSeqNodes.create once" in {
      val (objectFactory, createSeqNodes) = build()

      val step = objectFactory.createNodes(scope = Scope())

      whenReady(step) { _ =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
      }(config = patienceConfig)
    }
  }

  "createParams" must {
    "throw exception" in {
      val scope = mock[IScope]
      val (objectFactory, _) = build()

      a[RuntimeException] must be thrownBy Await.result(objectFactory.createParams(scope), finiteTimeout)
    }
  }

  private def build(nextInt: Int = 0) = {
    val randomNumberGenerator = new StubRngBinding(nextInt = nextInt)
    val createSeqNodes = new StubCreateSeqNodesBinding
    val injector = testInjector(
      randomNumberGenerator,
      createSeqNodes,
      new DecisionBindings,
      new StubLookupChildrenWithFutures,
      new StubCreateNodeBinding,
      new StubSelectionStrategyBinding
    )
    (injector.getInstance(classOf[ObjectFactory]), createSeqNodes.stub)
  }
}