package nodes.helpers

import models.common.{IScope, Node}

trait ICreateSeqNodes {

  def createSeq(possibleChildren: => Seq[ICreateChildNodes],
                scope: IScope,
                saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
                acc: Seq[Node] = Seq.empty,
                factoryLimit: Int
                 ): (IScope, Seq[Node])
}
