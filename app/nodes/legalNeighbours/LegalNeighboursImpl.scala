package nodes.legalNeighbours

import nodes.helpers.{ICreateChildNodes, IScope}

class LegalNeighboursImpl(override val neighbours: Seq[ICreateChildNodes]) extends LegalNeightbours {
  override def fetchLegalNeighbours(scope: IScope): Seq[ICreateChildNodes] = LegalNeighboursImpl.allLegalNeighbours(scope, neighbours)
}

object LegalNeighboursImpl {
  def allLegalNeighbours(scope: IScope, neighbours: Seq[ICreateChildNodes]): Seq[ICreateChildNodes] = {
    val memo: IScope => Seq[ICreateChildNodes] = {
      def calculate(f: IScope => Seq[ICreateChildNodes])(scope: IScope): Seq[ICreateChildNodes] = {
        neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      }
      Memoize.Y(calculate)
    }

    memo(scope.incrementDepth).intersect(neighbours) // Only return legal moves that are neighbours
  }
}