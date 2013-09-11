package nodes.helpers

import nodes.Node

trait CreateChildNodes {
  def create(scope: Scope): Node

  def updateScope(scope: Scope): Scope = scope

  val neighbours: Seq[CreateChildNodes]

  val legalNeighbours: Scope => Seq[CreateChildNodes] = {
    def inner(f: Scope => Seq[CreateChildNodes])(scope: Scope): Seq[CreateChildNodes] = {
      neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
    }
    Memoize.Y(inner)
  }

  val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = {
      if (scope.hasDepthRemaining) neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      else false
    }
    Memoize.Y(inner)
  }
}