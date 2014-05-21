package nodes.helpers

import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours

trait ICreateChildNodes {
  val legalNeighbours: LegalNeighbours

  val neighbours: Seq[ICreateChildNodes] // TODO should the collection type be Array?

  def create(scope: IScope): Node

  def updateScope(scope: IScope): IScope
}