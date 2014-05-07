package nodes.helpers

import models.domain.scala.Empty
import scala.annotation.tailrec
import models.domain.common.Node

trait ICreateChildNodes {
  val neighbours: Seq[ICreateChildNodes]

  def create(scope: IScope): Node = Empty()

  def updateScope(scope: IScope): IScope

  def canTerminateInStepsRemaining(scope: IScope): Boolean
}