package decision

import ai.RandomNumberGenerator
import ai.SelectionStrategy
import ai.aco.Aco
import composition.TestComposition
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers.any
import org.mockito.Mockito._

import scala.concurrent.Future

final class CreateSeqNodesImplSpec extends TestComposition {

  "create step" must {
    "calls create on factory once given only space for 1 func in obj and mocked rng the same" in {
      val (possibleChildren, createSeqNodes, scope, createNode, _) = build(maxFuncsInObject = 1, nextInt = 1)

      val result = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(result) { r =>
        verify(createNode, times(1)).create(possibleChildren = possibleChildren, scope = scope)
        r.instructions.length must equal(1)
      }(config = patienceConfig)
    }

    "calls create on factory twice given space for 2 func in obj and mocked rng the same" in {
      val (possibleChildren, createSeqNodes, scope, createNode, _) = build(maxFuncsInObject = 2, nextInt = 2)

      val result = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(result) { r =>
        verify(createNode, times(2)).create(possibleChildren = possibleChildren, scope = scope)
        r.instructions.length must equal(2)
      }(config = patienceConfig)
    }

    "calls create on factory once given space for 2 func in obj but rng mocked to 1" in {
      val (possibleChildren, createSeqNodes, scope, createNode, _) = build(maxFuncsInObject = 2, nextInt = 1)

      val result = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(result) { r =>
        verify(createNode, times(1)).create(possibleChildren = possibleChildren, scope = scope)
        r.instructions.length must equal(1)
      }(config = patienceConfig)
    }

    "calls create on factory once given space for 2 func in obj and a rng mocked to 2 but 1 pre-made node already added" in {
      val (possibleChildren, createSeqNodes, scope, createNode, instruction) = build(maxFuncsInObject = 2, nextInt = 2)

      val result = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        initAcc = Seq(instruction),
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(result) { r =>
        verify(createNode, times(1)).create(possibleChildren = possibleChildren, scope = scope)
        r.instructions.length must equal(2)
      }(config = patienceConfig)
    }
  }

  def build(maxFuncsInObject: Int, nextInt: Int) = {
    val scope = mock[IScope]
    when(scope.maxFuncsInObject).thenReturn(maxFuncsInObject)
    val instruction = mock[Step]
    val decision = mock[Decision]
    when(decision.updateScope(scope)).thenReturn(scope)
    when(decision.createStep(any[Scope])).thenReturn(Future.successful(instruction))
    val rng = mock[RandomNumberGenerator]
    when(rng.nextBoolean).thenReturn(true)
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val ai: SelectionStrategy = Aco(rng)
    val createNode = mock[CreateNode]
    when(createNode.create(any[Future[Set[Decision]]], any[Scope])).thenReturn(Future.successful(scope, instruction))
    val possibleChildren = Future.successful(Set(decision))
    val createSeqNodes = CreateSeqNodesImpl(createNode, ai)

    (possibleChildren, createSeqNodes, scope, createNode, instruction)
  }
}