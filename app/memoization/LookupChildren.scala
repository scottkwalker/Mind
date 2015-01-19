package memoization

import models.common.IScope
import models.domain.scala.FactoryLookup
import replaceEmpty.ReplaceEmpty
import utils.PozInt
import scala.concurrent.Future

trait LookupChildren {

  val factoryLookup: FactoryLookup

  val repository: RepositoryWithFutures

  def fetch(scope: IScope, childrenToChooseFrom: Set[PozInt]): Future[Set[ReplaceEmpty]]

  def fetch(scope: IScope, parent: PozInt): Future[Set[PozInt]]

  def size: Int
}
