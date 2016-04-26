package decision

import models.common.IScope
import models.domain.Step

final case class AccumulateInstructions(instructions: Seq[Step], scope: IScope)
