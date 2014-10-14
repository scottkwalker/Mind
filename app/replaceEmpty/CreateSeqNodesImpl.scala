package replaceEmpty

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Instruction
import scala.annotation.tailrec

final case class CreateSeqNodesImpl @Inject()(createNode: CreateNode, ai: SelectionStrategy) extends CreateSeqNodes {

  @tailrec
  def create(possibleChildren: => Seq[ReplaceEmpty],
                scope: IScope,
                saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
                acc: Seq[Instruction] = Seq.empty, // Default the accumulator to empty.
                factoryLimit: Int
                 ): (IScope, Seq[Instruction]) = {
    if (ai.canAddAnother(acc.length, factoryLimit)) {
      val (updatedScope, child) = createNode.create(possibleChildren, scope)
      create(possibleChildren,
        updatedScope,
        saveAccLengthInScope,
        acc :+ child,
        factoryLimit
      )
    }
    else {
      val updatedScope = saveAccLengthInScope match {
        case Some(f) => f(scope, acc.length)
        case None => scope
      }
      (updatedScope, acc)
    }
  }
}