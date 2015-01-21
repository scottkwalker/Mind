package memoization

import models.common.IScope
import models.domain.scala.FactoryLookup
import replaceEmpty.ReplaceEmpty
import utils.PozInt
import scala.concurrent.Future

trait LookupChildrenWithFutures {

  val factoryLookup: FactoryLookup

  val repository: Memoize2[IScope, PozInt, Future[Boolean]]

  def fetch(scope: IScope, childrenToChooseFrom: Set[PozInt]): Future[Set[ReplaceEmpty]]

  def fetch(scope: IScope, parent: PozInt): Future[Set[PozInt]]

  def size: Int

  def sizeOfCalculated: Int
}