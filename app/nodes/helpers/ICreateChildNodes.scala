package nodes.helpers

import nodes.{Empty, Node}

trait ICreateChildNodes {
  val neighbours: Seq[ICreateChildNodes]

  val memoizeCanTerminateInStepsRemaining: MemoizeDi[Boolean]

  def create(scope: IScope): Node = Empty()

  def create(scope: IScope, premade: Option[Seq[ICreateChildNodes]]): Node = Empty()

  def updateScope(scope: IScope): IScope


  val legalNeighbours: IScope => Seq[ICreateChildNodes] = {
    def inner(f: IScope => Seq[ICreateChildNodes])(scope: IScope): Seq[ICreateChildNodes] = {
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
    def calc = scope.hasDepthRemaining match {
      case true => neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      case false => false
    }
    memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, calc)
  }
}