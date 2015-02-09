package decision

import models.common.IScope
import models.domain.Step

import scala.concurrent.Future

trait CreateSeqNodes {

  def create(possibleChildren: => Future[Set[Decision]],
             scope: IScope,
             acc: Seq[Step] = Seq.empty,
             factoryLimit: Int
              ): Future[AccumulateInstructions]
}
