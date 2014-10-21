package memoization

import replaceEmpty.ReplaceEmpty
import models.common.IScope

trait LookupNeighbours {

  def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ReplaceEmpty]

  def fetch(scope: IScope, currentNode: Int): Seq[Int]
}
