package memoization

import decision.Decision
import models.common.IScope
import models.domain.scala.FactoryLookup
import utils.PozInt

import scala.concurrent.Future

trait LookupChildrenWithFutures {

  val factoryLookup: FactoryLookup

  def get(
      scope: IScope, childrenToChooseFrom: Set[PozInt]): Future[Set[Decision]]

  def get(scope: IScope, parent: PozInt): Future[Set[PozInt]]

  def size: Int
}
