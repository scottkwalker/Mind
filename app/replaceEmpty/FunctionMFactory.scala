package replaceEmpty

import models.common.IScope
import models.domain.Instruction

import scala.concurrent.Future

trait FunctionMFactory extends ReplaceEmpty {

  def createParams(scope: IScope): Future[AccumulateInstructions]

  def createNodes(scope: IScope): Future[AccumulateInstructions]
}