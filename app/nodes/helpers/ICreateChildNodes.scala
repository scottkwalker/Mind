package nodes.helpers

import nodes.Node
import models.domain.scala.Empty
import scala.annotation.tailrec

trait ICreateChildNodes {
  val neighbours: Seq[ICreateChildNodes]

  def create(scope: IScope): Node = Empty()

  def create(scope: IScope, premadeChildren: Seq[ICreateChildNodes]): Node = Empty()

  def updateScope(scope: IScope): IScope

  def legalNeighbours(scope: IScope, neighbours: Seq[ICreateChildNodes]): Seq[ICreateChildNodes]  = {
    val legalNeighboursMemo: IScope => Seq[ICreateChildNodes] = {
      def inner(f: IScope => Seq[ICreateChildNodes])(scope: IScope): Seq[ICreateChildNodes] = {
        neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      }
      Memoize.Y(inner)
    }

    legalNeighboursMemo(scope.incrementDepth).intersect(neighbours) // Only return legal moves that are neighbours
  }

  def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    if (scope.hasDepthRemaining) neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
    else false
  }
}