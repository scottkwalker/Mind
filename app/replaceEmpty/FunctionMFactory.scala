package replaceEmpty

import models.common.IScope
import models.domain.Instruction
import utils.PozInt

import scala.concurrent.Future

trait FunctionMFactory extends ReplaceEmpty {

  def createParams(scope: IScope): Future[AccumulateInstructions]

  def createNodes(scope: IScope): Future[AccumulateInstructions]
}

object FunctionMFactory {

  val id = PozInt(2)
}