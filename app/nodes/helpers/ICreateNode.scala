package nodes.helpers

import models.common.{IScope, Node}

trait ICreateNode {

  def create(possibleChildren: Seq[ICreateChildNodes], scope: IScope): (IScope, Node)
}
