package replaceEmpty

import models.common.IScope
import models.domain.Instruction

import scala.concurrent.Future

trait FunctionMFactory extends ReplaceEmpty {

  def createParams(scope: IScope): Future[(IScope, Seq[Instruction])]

  def createNodes(scope: IScope): Future[(IScope, Seq[Instruction])]
}
