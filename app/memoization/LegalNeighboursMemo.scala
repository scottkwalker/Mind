package memoization

import factory.ICreateChildNodes
import models.common.IScope

trait LegalNeighboursMemo {

  def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes]

  def fetch(scope: IScope, currentNode: Int): Seq[Int]
}
