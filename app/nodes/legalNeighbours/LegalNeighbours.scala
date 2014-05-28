package nodes.legalNeighbours

import nodes.helpers.{ICreateChildNodes, IScope}

trait LegalNeighbours {
  def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes]
}