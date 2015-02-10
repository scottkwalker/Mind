package memoization

import decision.Decision
import models.common.IScope
import models.domain.scala.FactoryLookup
import utils.PozInt

trait LookupChildren {

  val factoryLookup: FactoryLookup

  def get(scope: IScope, childrenToChooseFrom: Set[PozInt]): Set[Decision]

  def get(scope: IScope, parent: PozInt): Set[PozInt]

  def size: Int
}
