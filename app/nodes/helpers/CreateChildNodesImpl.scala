package nodes.helpers

import models.domain.common.Node
import models.domain.scala.Empty

abstract class CreateChildNodesImpl extends ICreateChildNodes {
  override def create(scope: IScope): Node = Empty()
}
