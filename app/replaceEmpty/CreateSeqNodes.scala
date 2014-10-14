package replaceEmpty

import models.common.IScope
import models.domain.Node

trait CreateSeqNodes {

  def create(possibleChildren: => Seq[ReplaceEmpty],
                scope: IScope,
                saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
                acc: Seq[Node] = Seq.empty,
                factoryLimit: Int
                 ): (IScope, Seq[Node])
}
