package nodes.helpers

import models.domain.common.Node

trait ICreateSeqNodes {
  def createSeq(possibleChildren: => Seq[ICreateChildNodes],
                scope: IScope,
                saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
                acc: Seq[Node] = Seq.empty,
                factoryLimit: Int
                 ): (IScope, Seq[Node])
}
