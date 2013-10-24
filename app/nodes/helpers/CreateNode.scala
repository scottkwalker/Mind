package nodes.helpers

import com.google.inject.Inject
import ai.Ai
import nodes.Node

case class CreateNode @Inject()() {
  def create(possibleChildren: Seq[CreateChildNodes], scope: Scope, ai: Ai): (Scope, Node) = {
    val factory = ai.chooseChild(possibleChildren, scope)
    val child = factory.create(scope)
    val updatedScope = factory.updateScope(scope)
    (updatedScope, child)
  }
}