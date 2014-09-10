package factory

import models.common.IScope
import models.domain.Node

trait CreateNode {

  def create(possibleChildren: Seq[ReplaceEmpty], scope: IScope): (IScope, Node)
}
