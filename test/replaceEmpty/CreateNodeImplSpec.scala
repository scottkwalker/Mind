package replaceEmpty

import composition.CreateNodeBinding
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import models.common.Scope
import org.mockito.Mockito._

import scala.concurrent.Future

final class CreateNodeImplSpec extends TestComposition {

  "create" must {
    "calls chooseChild on ai" in {
      val (_, scope, ai, possibleChildren, createNode) = build

      val sut = createNode

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(ai, times(1)).chooseChild(possibleChildren)
      }(config = patienceConfig)
    }

    "calls updateScope" in {
      val (replaceEmpty, scope, ai, possibleChildren, createNode) = build

      val sut = createNode

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(replaceEmpty, times(1)).updateScope(scope)
      }(config = patienceConfig)
    }

    "calls create on factory" in {
      val (replaceEmpty, scope, ai, possibleChildren, createNode) = build

      val sut = createNode

      whenReady(sut.create(possibleChildren, scope)) { _ =>
        verify(replaceEmpty, times(1)).create(scope)
      }(config = patienceConfig)
    }
  }

  private def build = {
    val selectionStrategyBinding = new StubSelectionStrategyBinding
    val possibleChildren = Future.successful(Set(selectionStrategyBinding.stubReplaceEmpty))
    val createNode = testInjector(
      selectionStrategyBinding,
      new CreateNodeBinding
    ).getInstance(classOf[CreateNode])

    (selectionStrategyBinding.stubReplaceEmpty, Scope(), selectionStrategyBinding.stub, possibleChildren, createNode)
  }
}