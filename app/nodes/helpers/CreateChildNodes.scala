package nodes.helpers

import nodes.{Empty, Node}

trait CreateChildNodes {
  def create(scope: Scope, premade: Option[Node]): Node

  def updateScope(scope: Scope): Scope = scope

  val neighbours: Seq[CreateChildNodes]

  val legalNeighbours: Scope => Seq[CreateChildNodes] = {
    def inner(f: Scope => Seq[CreateChildNodes])(scope: Scope): Seq[CreateChildNodes] = {
      neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
    }
    Memoize.Y(inner)
  }

  protected val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = {
      scope.hasDepthRemaining match {
        case true => neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
        case false => false
      }
    }
    Memoize.Y(inner)
  }
}