package nodes.helpers

import ai.Ai
import nodes.Node

trait ICreateNode {
  def create(possibleChildren: Seq[ICreateChildNodes], scope: IScope, ai: Ai): (IScope, Node)
}
