package memoization

import models.common.IScope
import replaceEmpty.ReplaceEmpty
import scala.concurrent.Future

trait LookupChildren {

  def fetch(scope: IScope, childrenToChooseFrom: Set[Int]): Future[Set[ReplaceEmpty]]

  def fetch(scope: IScope, parent: Int): Future[Set[Int]]
}
