package nodes.helpers

import com.google.inject.Inject
import ai.Ai
import nodes.Node

case class CreateNode @Inject() () {
  def create(self: CreateChildNodes, scope: Scope, ai: Ai): (Scope, Node) = {
    val factory = ai.chooseChild(self, scope)
    val updatedScope = factory.updateScope(scope)
    val child = factory.create(updatedScope)
    (updatedScope, child)
  }
}