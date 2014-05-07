package nodes.helpers

import nodes.legalNeighbours.Memoize

abstract class CreateChildNodesImpl extends ICreateChildNodes {
  override def legalNeighbours(scope: IScope, neighbours: Seq[ICreateChildNodes]): Seq[ICreateChildNodes]  = {
    val legalNeighboursMemo: IScope => Seq[ICreateChildNodes] = {
      def inner(f: IScope => Seq[ICreateChildNodes])(scope: IScope): Seq[ICreateChildNodes] = {
        neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      }
      Memoize.Y(inner)
    }

    legalNeighboursMemo(scope.incrementDepth).intersect(neighbours) // Only return legal moves that are neighbours
  }
}
