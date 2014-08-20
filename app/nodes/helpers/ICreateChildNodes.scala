package nodes.helpers

import models.common.{IScope, Node}

trait ICreateChildNodes {

  val neighbourIds: Seq[Int] // TODO should the collection type be Array?

  def create(scope: IScope): Node

  def updateScope(scope: IScope): IScope
}