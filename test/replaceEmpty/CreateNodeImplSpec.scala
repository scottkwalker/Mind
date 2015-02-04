package replaceEmpty

import ai.SelectionStrategy
import composition.{StubReplaceEmptyBinding, StubSelectionStrategyBinding, TestComposition}
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito._

import scala.concurrent.Future

final class CreateNodeImplSpec extends TestComposition {

  "create" must {
    "calls chooseChild on ai" in {
      val (_, scope, ai, possibleChildren) = build

      val sut = CreateNodeImpl(ai)

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(ai, times(1)).chooseChild(possibleChildren)
      }(config = patienceConfig)
    }

    "calls updateScope" in {
      val (replaceEmpty, scope, ai, possibleChildren) = build

      val sut = CreateNodeImpl(ai)

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(replaceEmpty, times(1)).updateScope(scope)
      }(config = patienceConfig)
    }

    "calls create on factory" in {
      val (replaceEmpty, scope, ai, possibleChildren) = build

      val sut = CreateNodeImpl(ai)

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(replaceEmpty, times(1)).create(scope)
      }(config = patienceConfig)
    }
  }

  private def build = {
    val selectionStrategyBinding = new StubSelectionStrategyBinding
    val possibleChildren = Future.successful(Set(selectionStrategyBinding.stubReplaceEmpty))
    testInjector(
      selectionStrategyBinding
    ).getInstance(classOf[CreateNode])

    (selectionStrategyBinding.stubReplaceEmpty, Scope(), selectionStrategyBinding.stub, possibleChildren)
  }
}