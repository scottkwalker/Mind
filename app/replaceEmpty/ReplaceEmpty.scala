package replaceEmpty

import models.common.IScope
import models.domain.Instruction
import scala.concurrent.Future

trait ReplaceEmpty {

  val nodesToChooseFrom: Set[Int] // TODO should the collection type be Array?

  def create(scope: IScope): Future[Instruction]

  def updateScope(scope: IScope): IScope
}