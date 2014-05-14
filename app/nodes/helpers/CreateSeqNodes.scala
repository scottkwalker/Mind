package nodes.helpers

import com.google.inject.Inject
import scala.annotation.tailrec
import ai.{IAi, IRandomNumberGenerator}
import models.domain.common.Node

case class CreateSeqNodes @Inject()(createNode: ICreateNode, ai: IAi) extends ICreateSeqNodes {
  @tailrec
  final def createSeq(possibleChildren: => Seq[ICreateChildNodes],
                      scope: IScope,
                      saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
                      acc: Seq[Node] = Seq.empty, // Default the accumulator to empty.
                      factoryLimit: Int
                       ): (IScope, Seq[Node]) = {
    if (ai.canAddAnother(acc.length, factoryLimit)) {
      val (updatedScope, child) = createNode.create(possibleChildren, scope, ai)
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