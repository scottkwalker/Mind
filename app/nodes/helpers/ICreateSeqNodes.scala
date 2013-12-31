package nodes.helpers

import scala._

/**
 * Created by valtechuk on 31/12/2013.
 */
trait ICreateSeqNodes {
  def createSeq(possibleChildren: Seq[ICreateChildNodes],
                scope: IScope,
                saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
                acc: Seq[Node] = Seq[Node](),
                factoryLimit: Int
                 ): (IScope, Seq[Node])
}
