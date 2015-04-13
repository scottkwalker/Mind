package decision

import composition.CreateNodeBinding
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import org.mockito.Mockito._

import scala.concurrent.Future

final class CreateNodeImplSpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "calls chooseChild on ai" in {
      val (_, scope, ai, possibleChildren, createNode) = build

      whenReady(createNode.create(possibleChildren, scope)) { _ =>
        verify(ai, times(1)).chooseChild(possibleChildren)
        verifyNoMoreInteractions(ai)
      }(config = patienceConfig)
    }

    "calls fillEmptySteps and updateScope once" in {
      val (decision, scope, _, possibleChildren, createNode) = build

      whenReady(createNode.create(possibleChildren, scope)) { _ =>
        verify(decision, times(1)).fillEmptySteps(scope)
        verify(decision, times(1)).updateScope(scope)
        verifyNoMoreInteractions(decision)
      }(config = patienceConfig)
    }
  }

  private def build = {
    val selectionStrategyBinding = new StubSelectionStrategyBinding
    val possibleChildren = Future.successful(Set(selectionStrategyBinding.stubDecision))
    val scope = mock[IScope]
    val createNode = testInjector(
      selectionStrategyBinding,
      new CreateNodeBinding // The system under test
    ).getInstance(classOf[CreateNode])

    (selectionStrategyBinding.stubDecision, scope, selectionStrategyBinding.stub, possibleChildren, createNode)
  }
}