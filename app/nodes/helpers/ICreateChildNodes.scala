package nodes.helpers

import models.domain.scala.Empty
import scala.annotation.tailrec
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours

trait ICreateChildNodes {
  val legalNeighbours: LegalNeighbours

  val neighbours: Seq[ICreateChildNodes]

  def create(scope: IScope): Node = Empty()

  def updateScope(scope: IScope): IScope

  def canTerminateInStepsRemaining(scope: IScope): Boolean
}