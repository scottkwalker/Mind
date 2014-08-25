package factory

import models.common.IScope
import models.domain.Node

trait ICreateChildNodes {

  val neighbourIds: Seq[Int] // TODO should the collection type be Array?

  def create(scope: IScope): Node

  def updateScope(scope: IScope): IScope
}