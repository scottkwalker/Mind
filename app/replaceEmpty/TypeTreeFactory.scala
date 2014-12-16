package replaceEmpty

import models.common.IScope
import models.domain.Instruction

import scala.concurrent.Future

trait TypeTreeFactory extends ReplaceEmpty {

  def create(scope: IScope, premadeChildren: Seq[ReplaceEmpty]): Future[Instruction]

  def createNodes(scope: IScope, acc: Seq[Instruction] = Seq.empty): Future[AccumulateInstructions]
}
