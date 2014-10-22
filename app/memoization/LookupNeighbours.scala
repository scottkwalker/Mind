package memoization

import models.common.IScope
import replaceEmpty.ReplaceEmpty

trait LookupNeighbours {

  def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ReplaceEmpty]

  def fetch(scope: IScope, currentNode: Int): Seq[Int]
}
