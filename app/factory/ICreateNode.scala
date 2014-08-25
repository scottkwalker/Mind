package factory

import models.common.IScope
import models.domain.Node

trait ICreateNode {

  def create(possibleChildren: Seq[ICreateChildNodes], scope: IScope): (IScope, Node)
}
