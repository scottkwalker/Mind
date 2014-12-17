package replaceEmpty

import models.common.IScope
import models.domain.Instruction
import utils.PozInt
import scala.concurrent.Future

trait ReplaceEmpty {

  val nodesToChooseFrom: Set[PozInt]

  def create(scope: IScope): Future[Instruction]

  def updateScope(scope: IScope): IScope
}