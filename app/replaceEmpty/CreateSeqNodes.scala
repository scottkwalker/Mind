package replaceEmpty

import models.common.IScope
import models.domain.Instruction

import scala.concurrent.Future

trait CreateSeqNodes {

  def create(possibleChildren: => Future[Set[ReplaceEmpty]],
             scope: IScope,
             acc: Seq[Instruction] = Seq.empty,
             factoryLimit: Int
              ): Future[AccumulateInstructions]
}
