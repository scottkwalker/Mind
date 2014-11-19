package replaceEmpty

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.{Scope, IScope}
import models.domain.Instruction
import models.domain.scala.Empty
import utils.Timeout.finiteTimeout
import scala.annotation.tailrec
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

final case class CreateSeqNodesImpl @Inject()(createNode: CreateNode, ai: SelectionStrategy) extends CreateSeqNodes {

  def create(possibleChildren: => Future[Seq[ReplaceEmpty]],
             scope: IScope,
             saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
             acc: Seq[Instruction] = Seq.empty, // Default the accumulator to empty.
             factoryLimit: Int
              ): Future[(IScope, Seq[Instruction])] = {
    if (ai.canAddAnother(acc.length, factoryLimit)) {
      createNode.create(possibleChildren, scope).flatMap {
        case (updatedScope, child) =>
          create(possibleChildren,
            updatedScope,
            saveAccLengthInScope,
            acc :+ child,
            factoryLimit
          )
      }
    }
    else {
      val updatedScope = saveAccLengthInScope match {
        case Some(f) => f(scope, acc.length)
        case None => scope
      }
      Future.successful((updatedScope, acc))
    }
  }
}