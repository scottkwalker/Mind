package decision

import ai.RandomNumberGenerator
import ai.SelectionStrategy
import ai.aco.Aco
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.domain.Step
import org.mockito.Matchers.any
import org.mockito.Mockito._

import scala.concurrent.Future

final class CreateSeqNodesImplSpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "call create on factory once if there is only space for 1 func in obj and mocked rng the same" in {
      val (possibleChildren, createSeqNodes, scope, createNode, _) = build(maxFuncsInObject = 1, nextInt = 1)

      val created = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        initAcc = Seq.empty[Step],
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(created) {
        case AccumulateInstructions(instructions, _) =>
          verify(createNode, times(1)).create(possibleChildren = possibleChildren, scope = scope)
          verifyNoMoreInteractions(createNode)
          instructions.length must equal(1)
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "call create on factory twice if there is space for 2 func in obj and mocked rng the same" in {
      val (possibleChildren, createSeqNodes, scope, createNode, _) = build(maxFuncsInObject = 2, nextInt = 2)

      val created = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        initAcc = Seq.empty[Step],
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(created) {
        case AccumulateInstructions(instructions, _) =>
          verify(createNode, times(2)).create(possibleChildren = possibleChildren, scope = scope)
          verifyNoMoreInteractions(createNode)
          instructions.length must equal(2)
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "call create on factory once if there is space for 2 func in obj but rng mocked to 1" in {
      val (possibleChildren, createSeqNodes, scope, createNode, _) = build(maxFuncsInObject = 2, nextInt = 1)

      val created = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        initAcc = Seq.empty[Step],
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(created) {
        case AccumulateInstructions(instructions, _) =>
          verify(createNode, times(1)).create(possibleChildren = possibleChildren, scope = scope)
          verifyNoMoreInteractions(createNode)
          instructions.length must equal(1)
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "call create on factory once if there is space for 2 func in obj and a rng mocked to 2 but 1 pre-made node already added" in {
      val (possibleChildren, createSeqNodes, scope, createNode, instruction) = build(maxFuncsInObject = 2, nextInt = 2)

      val created = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        initAcc = Seq(instruction),
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(created) {
        case AccumulateInstructions(instructions, _) =>
          verify(createNode, times(1)).create(possibleChildren = possibleChildren, scope = scope)
          verifyNoMoreInteractions(createNode)
          instructions.length must equal(2)
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }
  }

  def build(maxFuncsInObject: Int, nextInt: Int) = {
    val scope = mock[IScope]
    when(scope.maxFuncsInObject).thenReturn(maxFuncsInObject)
    val instruction = mock[Step]
    val decision = mock[Decision]
    when(decision.updateScope(scope)).thenReturn(scope)
    when(decision.fillEmptySteps(any[IScope])).thenReturn(Future.successful(instruction))
    val rng = mock[RandomNumberGenerator]
    when(rng.nextBoolean).thenReturn(true)
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val ai: SelectionStrategy = Aco(rng)
    val createNode = mock[CreateNode]
    when(createNode.create(any[Future[Set[Decision]]], any[IScope])).thenReturn(Future.successful(scope, instruction))
    val possibleChildren = Future.successful(Set(decision))
    val createSeqNodes = CreateSeqNodesImpl(createNode, ai)

    (possibleChildren, createSeqNodes, scope, createNode, instruction)
  }
}