package decision

import models.common.IScope
import models.domain.Step
import utils.PozInt

import scala.concurrent.Future

trait TypeTreeFactory extends Decision {

  def create(scope: IScope, premadeChildren: Seq[Decision]): Future[Step]

  def createNodes(scope: IScope, acc: Seq[Step] = Seq.empty): Future[AccumulateInstructions]
}

object TypeTreeFactory {

  val id = PozInt(4)
}