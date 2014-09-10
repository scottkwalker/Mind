package factory

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Node
import scala.annotation.tailrec

final case class CreateSeqNodes @Inject()(createNode: ICreateNode, ai: SelectionStrategy) extends ICreateSeqNodes {

  @tailrec
  def createSeq(possibleChildren: => Seq[ReplaceEmpty],
                scope: IScope,
                saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
                acc: Seq[Node] = Seq.empty, // Default the accumulator to empty.
                factoryLimit: Int
                 ): (IScope, Seq[Node]) = {
    if (ai.canAddAnother(acc.length, factoryLimit)) {
      val (updatedScope, child) = createNode.create(possibleChildren, scope)
      createSeq(possibleChildren,
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