package nodes.helpers

import models.domain.common.Node

trait ICreateNode {

  def create(possibleChildren: Seq[ICreateChildNodes], scope: IScope): (IScope, Node)
}
