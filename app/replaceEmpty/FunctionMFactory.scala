package replaceEmpty

import models.common.IScope
import models.domain.Instruction

import scala.concurrent.Future

trait FunctionMFactory extends ReplaceEmpty {

  def createParams(scope: IScope, acc: Seq[Instruction] = Seq.empty): Future[(IScope, Seq[Instruction])]

  def createNodes(scope: IScope, acc: Seq[Instruction] = Seq.empty): Future[(IScope, Seq[Instruction])]
}
