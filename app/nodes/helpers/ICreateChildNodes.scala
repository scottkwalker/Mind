package nodes.helpers

import models.domain.scala.Empty
import scala.annotation.tailrec
import models.domain.common.Node

trait ICreateChildNodes {
  val neighbours: Seq[ICreateChildNodes]

  def create(scope: IScope): Node = Empty()

  def create(scope: IScope, premadeChildren: Seq[ICreateChildNodes]): Node = Empty() // TODO need param for params!

  def updateScope(scope: IScope): IScope

  def legalNeighbours(scope: IScope, neighbours: Seq[ICreateChildNodes]): Seq[ICreateChildNodes]

  def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    if (scope.hasDepthRemaining) neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
    else false
  }
}