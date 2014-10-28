package memoization

import models.common.IScope
import replaceEmpty.ReplaceEmpty
import scala.concurrent.Future

trait LookupNeighbours {

  def fetch(scope: IScope, neighbours: Seq[Int]): Future[Seq[ReplaceEmpty]]

  def fetch(scope: IScope, currentNode: Int): Future[Seq[Int]]
}
