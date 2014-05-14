package nodes.legalNeighbours

import nodes.helpers.{ICreateChildNodes, IScope}

class LegalNeighboursImpl extends LegalNeighbours {
  override def fetch(scope: IScope, neighbours: Seq[ICreateChildNodes]): Seq[ICreateChildNodes] ={
    val memo: IScope => Seq[ICreateChildNodes] = {
      def calculate(f: IScope => Seq[ICreateChildNodes])(scope: IScope): Seq[ICreateChildNodes] = {
        if (scope.hasDepthRemaining) neighbours.filter {
          n => n.neighbours.isEmpty || fetch(scope.incrementDepth, n.neighbours).length > 0
        }
        else Seq.empty
      }
      Memoize.Y(calculate)
    }

    memo(scope.incrementDepth).intersect(neighbours) // Only return legal moves that are neighbours
  }

  // TODO write to disk
}