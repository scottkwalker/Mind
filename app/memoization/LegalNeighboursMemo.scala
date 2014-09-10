package memoization

import factory.ReplaceEmpty
import models.common.IScope

trait LegalNeighboursMemo {

  def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ReplaceEmpty]

  def fetch(scope: IScope, currentNode: Int): Seq[Int]
}
