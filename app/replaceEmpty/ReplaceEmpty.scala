package replaceEmpty

import models.common.IScope
import models.domain.Instruction

trait ReplaceEmpty {

  val neighbourIds: Seq[Int] // TODO should the collection type be Array?

  def create(scope: IScope): Instruction

  def updateScope(scope: IScope): IScope
}