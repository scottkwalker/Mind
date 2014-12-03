package replaceEmpty

import models.common.IScope
import models.domain.Instruction

case class AccumulateInstructions(instructions: Seq[Instruction], scope: IScope)