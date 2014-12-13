package memoization

import models.common.IScope
import replaceEmpty.ReplaceEmpty
import scala.concurrent.Future

trait LookupChildren {

  def fetch(scope: IScope, childrenToChooseFrom: Seq[Int]): Future[Seq[ReplaceEmpty]]

  def fetch(scope: IScope, parent: Int): Future[Seq[Int]]
}
