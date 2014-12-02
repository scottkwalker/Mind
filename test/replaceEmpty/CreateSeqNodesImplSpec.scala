package replaceEmpty

import ai.aco.Aco
import ai.{RandomNumberGenerator, SelectionStrategy}
import composition.TestComposition
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers.any
import org.mockito.Mockito._
import scala.concurrent.Future

final class CreateSeqNodesImplSpec extends TestComposition {

  "create" must {
    "calls create on factory once given only space for 1 func in obj and mocked rng the same" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(1)
      val instruction = mock[Instruction]
      val replaceEmpty = mock[ReplaceEmpty]
      when(replaceEmpty.updateScope(scope)).thenReturn(scope)
      when(replaceEmpty.create(any[Scope])).thenReturn(Future.successful(instruction))
      val rng = mock[RandomNumberGenerator]
      when(rng.nextBoolean).thenReturn(true)
      when(rng.nextInt(any[Int])).thenReturn(1)
      val ai: SelectionStrategy = Aco(rng)
      val createNode = mock[CreateNode]
      when(createNode.create(any[Future[Seq[ReplaceEmpty]]], any[Scope])).thenReturn(Future.successful(scope, instruction))
      val possibleChildren = Future.successful(Seq(replaceEmpty))
      val createSeqNodes = CreateSeqNodesImpl(createNode, ai)

      val result = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(result) {
        case (_, nodes) =>
          verify(createNode, times(1)).create(possibleNodes = possibleChildren, scope = scope)
          nodes.length must equal(1)
      }
    }

    "calls create on factory twice given space for 2 func in obj and mocked rng the same" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(2)
      val instruction = mock[Instruction]
      val replaceEmpty = mock[ReplaceEmpty]
      when(replaceEmpty.updateScope(scope)).thenReturn(scope)
      when(replaceEmpty.create(any[Scope])).thenReturn(Future.successful(instruction))
      val cn = mock[CreateNode]
      when(cn.create(any[Future[Seq[ReplaceEmpty]]], any[Scope])).thenReturn(Future.successful(scope, instruction))
      val rng = mock[RandomNumberGenerator]
      when(rng.nextInt(any[Int])).thenReturn(2)
      when(rng.nextBoolean).thenReturn(true)
      val ai: SelectionStrategy = Aco(rng)
      val possibleChildren = Future.successful(Seq(replaceEmpty))
      val createSeqNodes = CreateSeqNodesImpl(cn, ai)

      val result = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(result) {
        case (_, nodes) =>
          verify(cn, times(2)).create(possibleNodes = possibleChildren, scope = scope)
          nodes.length must equal(2)
      }
    }

    "calls create on factory once given space for 2 func in obj but rng mocked to 1" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(2)
      val instruction = mock[Instruction]
      val replaceEmpty = mock[ReplaceEmpty]
      when(replaceEmpty.updateScope(scope)).thenReturn(scope)
      when(replaceEmpty.create(any[Scope])).thenReturn(Future.successful(instruction))
      val createNode = mock[CreateNode]
      when(createNode.create(any[Future[Seq[ReplaceEmpty]]], any[Scope])).thenReturn(Future.successful(scope, instruction))
      val rng = mock[RandomNumberGenerator]
      when(rng.nextInt(any[Int])).thenReturn(1)
      when(rng.nextBoolean).thenReturn(false, true)
      val ai: SelectionStrategy = Aco(rng)
      val possibleChildren = Future.successful(Seq(replaceEmpty))
      val createSeqNodes = CreateSeqNodesImpl(createNode, ai)

      val result = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(result) {
        case (_, nodes) =>
          verify(createNode, times(1)).create(possibleNodes = possibleChildren, scope = scope)
          nodes.length must equal(1)
      }
    }

    "calls create on factory once given space for 2 func in obj and a rng mocked to 2 but 1 pre-made node already added" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(2)
      val instruction = mock[Instruction]
      val replaceEmpty = mock[ReplaceEmpty]
      when(replaceEmpty.updateScope(scope)).thenReturn(scope)
      when(replaceEmpty.create(any[Scope])).thenReturn(Future.successful(instruction))
      val createNode = mock[CreateNode]
      when(createNode.create(any[Future[Seq[ReplaceEmpty]]], any[Scope])).thenReturn(Future.successful(scope, instruction))
      val rng = mock[RandomNumberGenerator]
      when(rng.nextInt(any[Int])).thenReturn(2)
      when(rng.nextBoolean).thenReturn(true)
      val ai: SelectionStrategy = Aco(rng)
      val possibleChildren = Future.successful(Seq(replaceEmpty))
      val createSeqNodes = CreateSeqNodesImpl(createNode, ai)

      val result = createSeqNodes.create(possibleChildren = possibleChildren,
        initScope = scope,
        initAcc = Seq(instruction),
        factoryLimit = scope.maxFuncsInObject
      )

      whenReady(result) {
        case (_, nodes) =>
          verify(createNode, times(1)).create(possibleNodes = possibleChildren, scope = scope)
          nodes.length must equal(2)
      }
    }
  }
}