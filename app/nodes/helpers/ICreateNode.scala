package nodes.helpers

import ai.IAi
import nodes.Node

trait ICreateNode {
  def create(possibleChildren: Seq[ICreateChildNodes], scope: IScope, ai: IAi): (IScope, Node)
}
