package nodes.helpers

import com.google.inject.Inject
import ai.Ai
import nodes.Node

case class CreateNode @Inject()() extends ICreateNode {
  def create(possibleChildren: Seq[CreateChildNodes], scope: IScope, ai: Ai): (IScope, Node) = {
    val factory = ai.chooseChild(possibleChildren, scope)
    val child = factory.create(scope)
    val updatedScope = factory.updateScope(scope)
    (updatedScope, child)
  }
}