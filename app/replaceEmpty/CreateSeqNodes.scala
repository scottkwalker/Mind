package replaceEmpty

import models.common.IScope
import models.domain.Instruction

trait CreateSeqNodes {

  def create(possibleChildren: => Seq[ReplaceEmpty],
             scope: IScope,
             saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
             acc: Seq[Instruction] = Seq.empty,
             factoryLimit: Int
              ): (IScope, Seq[Instruction])
}
