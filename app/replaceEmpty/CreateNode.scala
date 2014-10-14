package replaceEmpty

import models.common.IScope
import models.domain.Instruction

trait CreateNode {

  def create(possibleChildren: Seq[ReplaceEmpty], scope: IScope): (IScope, Instruction)
}
