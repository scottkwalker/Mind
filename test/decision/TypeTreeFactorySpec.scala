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
import models.domain.scala.TypeTree
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class TypeTreeFactorySpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "return an instance of the expected type" in {
      val (typeTreeFactory, _, scope) = build()

      val step = typeTreeFactory.createStep(scope)

      whenReady(step) {
        _ mustBe a[TypeTree]
      }(config = patienceConfig)
    }

    "call CreateSeqNodes.create once" in {
      val (typeTreeFactory, createSeqNodes, scope) = build()

      val step = typeTreeFactory.create(scope, Seq.empty)

      whenReady(step) { _ =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
        verifyNoMoreInteractions(createSeqNodes)
      }(config = patienceConfig)
    }

    "returns 2 children if there is 1 premade and we allow 1 to be generated (by mocked dependency)" in {
      val premadeNode = mock[Step]
      val premadeChildren = {
        val decision = mock[Decision]
        when(decision.createStep(any[IScope])).thenReturn(Future.successful(premadeNode))
        Seq(decision)
      }
      val (typeTreeFactory, _, scope) = build(nextInt = 3)

      val step = typeTreeFactory.create(scope = scope, premadeChildren = premadeChildren)

      whenReady(step) {
        case TypeTree(child) =>
          child.length must equal(2) // 1 generated and 1 premade
          child.last must equal(premadeNode) // The premade are concatenated to the end of the seq
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }
  }

  "updateScope" must {
    "throw an exception" in {
      val (typeTreeFactory, _, scope) = build()
      a[RuntimeException] must be thrownBy typeTreeFactory.updateScope(scope)
    }
  }

  "createParams" must {
    "throw an exception" in {
      val (typeTreeFactory, _, scope) = build()

      a[RuntimeException] must be thrownBy typeTreeFactory.createParams(scope).futureValue
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
    (injector.getInstance(classOf[TypeTreeFactory]), createSeqNodes.stub, scope)
  }
}