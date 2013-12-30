package nodes.helpers

import nodes.{Empty, Node}

trait CreateChildNodes {
  val neighbours: Seq[CreateChildNodes]

  val memoizeCanTerminateInStepsRemaining: MemoizeDi

  def create(scope: IScope): Node = Empty()

  def create(scope: IScope, premade: Option[Seq[CreateChildNodes]]): Node = Empty()

  def updateScope(scope: IScope): IScope


  val legalNeighbours: IScope => Seq[CreateChildNodes] = {
    def inner(f: IScope => Seq[CreateChildNodes])(scope: IScope): Seq[CreateChildNodes] = {
      neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
    }
    Memoize.Y(inner)
  }
/*
  protected val canTerminateInStepsRemaining: IScope => Boolean = {
    def inner(f: IScope => Boolean)(scope: IScope): Boolean = {
      scope.hasDepthRemaining match {
        case true => neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
        case false => false
      }
    }
    Memoize.Y(inner)
  }
*/

  protected def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    def result = scope.hasDepthRemaining match {
      case true => neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      case false => false
    }
    memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
  }
}