package nodes.helpers

import nodes.{Empty, Node}

trait CreateChildNodes {
  val neighbours: Seq[CreateChildNodes]

  val memoizeCanTerminateInStepsRemaining: MemoizeDi

  def create(scope: Scope): Node = Empty()

  def create(scope: Scope, premade: Option[Seq[CreateChildNodes]]): Node = Empty()

  def updateScope(scope: Scope): Scope


  val legalNeighbours: Scope => Seq[CreateChildNodes] = {
    def inner(f: Scope => Seq[CreateChildNodes])(scope: Scope): Seq[CreateChildNodes] = {
      neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
    }
    Memoize.Y(inner)
  }
/*
  protected val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = {
      scope.hasDepthRemaining match {
        case true => neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
        case false => false
      }
    }
    Memoize.Y(inner)
  }
*/

  protected def canTerminateInStepsRemaining(scope: Scope): Boolean = {
    def result = scope.hasDepthRemaining match {
      case true => neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      case false => false
    }
    memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
  }
}