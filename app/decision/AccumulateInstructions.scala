package decision

import models.common.IScope
import models.domain.Step

case class AccumulateInstructions(instructions: Seq[Step], scope: IScope)