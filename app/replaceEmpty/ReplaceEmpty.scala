package replaceEmpty

import models.common.IScope
import models.domain.Node

trait ReplaceEmpty {

  val neighbourIds: Seq[Int] // TODO should the collection type be Array?

  def create(scope: IScope): Node

  def updateScope(scope: IScope): IScope
}