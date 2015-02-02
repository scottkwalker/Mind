package memoization

import models.common.IScope
import models.domain.scala.FactoryLookup
import replaceEmpty.ReplaceEmpty
import utils.PozInt

trait LookupChildren {

  val factoryLookup: FactoryLookup

  def get(scope: IScope, childrenToChooseFrom: Set[PozInt]): Set[ReplaceEmpty]

  def get(scope: IScope, parent: PozInt): Set[PozInt]

  def size: Int
}
