package nodes.legalNeighbours

import nodes.helpers.{ICreateChildNodes, IScope}

class LegalNeighboursImpl extends LegalNeighbours {
  override def fetch(scope: IScope, neighbours: Seq[ICreateChildNodes]): Seq[ICreateChildNodes] ={
    val memo: IScope => Seq[ICreateChildNodes] = {
      def calculate(f: IScope => Seq[ICreateChildNodes])(scope: IScope): Seq[ICreateChildNodes] = {
        neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      }
      Memoize.Y(calculate)
    }

    memo(scope.incrementDepth).intersect(neighbours) // Only return legal moves that are neighbours
  }
}