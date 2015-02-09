package decision

import models.common.IScope
import utils.PozInt

import scala.concurrent.Future

trait FunctionMFactory extends Decision {

  def createParams(scope: IScope): Future[AccumulateInstructions]

  def createNodes(scope: IScope): Future[AccumulateInstructions]
}

object FunctionMFactory {

  val id = PozInt(2)
}