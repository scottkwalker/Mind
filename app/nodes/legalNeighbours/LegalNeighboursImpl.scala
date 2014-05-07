package nodes.legalNeighbours

import nodes.helpers.{ICreateChildNodes, IScope}

class LegalNeighboursImpl(override val neighbours: Seq[ICreateChildNodes]) extends LegalNeightbours {
  override def fetchLegalNeighbours(scope: IScope): Seq[ICreateChildNodes]  = {
    val legalNeighboursMemo: IScope => Seq[ICreateChildNodes] = {
      def inner(f: IScope => Seq[ICreateChildNodes])(scope: IScope): Seq[ICreateChildNodes] = {
        neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      }
      Memoize.Y(inner)
    }

    legalNeighboursMemo(scope.incrementDepth).intersect(neighbours) // Only return legal moves that are neighbours
  }
}
