package memoization

import models.common.IScope
import replaceEmpty.ReplaceEmpty
import utils.PozInt
import scala.concurrent.Future

trait LookupChildren {

  def fetch(scope: IScope, childrenToChooseFrom: Set[PozInt]): Future[Set[ReplaceEmpty]]

  def fetch(scope: IScope, parent: PozInt): Future[Set[PozInt]]
}
