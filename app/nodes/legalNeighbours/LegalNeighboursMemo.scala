package nodes.legalNeighbours

import models.common.IScope
import nodes.helpers.ICreateChildNodes

trait LegalNeighboursMemo {

  def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes]

  def fetch(scope: IScope, currentNode: Int): Seq[Int]
}