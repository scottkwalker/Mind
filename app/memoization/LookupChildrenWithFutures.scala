package memoization

import models.common.IScope
import models.domain.scala.FactoryLookup
import replaceEmpty.ReplaceEmpty
import utils.PozInt
import scala.concurrent.Future

trait LookupChildrenWithFutures {

  val factoryLookup: FactoryLookup

  def get(scope: IScope, childrenToChooseFrom: Set[PozInt]): Future[Set[ReplaceEmpty]]

  def get(scope: IScope, parent: PozInt): Future[Set[PozInt]]

  def size: Int
}