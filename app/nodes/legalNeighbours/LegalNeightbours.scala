package nodes.legalNeighbours

import nodes.helpers.{ICreateChildNodes, IScope}

trait LegalNeightbours {
  val neighbours: Seq[ICreateChildNodes]
  def fetchLegalNeighbours(scope: IScope): Seq[ICreateChildNodes]
}
