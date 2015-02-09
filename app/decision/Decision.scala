package decision

import models.common.IScope
import models.domain.Step
import utils.PozInt

import scala.concurrent.Future

trait Decision {

  val nodesToChooseFrom: Set[PozInt]

  def createStep(scope: IScope): Future[Step]

  def updateScope(scope: IScope): IScope
}
