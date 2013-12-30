package nodes.helpers

import com.google.inject.Inject
import ai.{IAi, AiCommon}
import nodes.Node

case class CreateNode @Inject()() extends ICreateNode {
  def create(possibleChildren: Seq[ICreateChildNodes], scope: IScope, ai: IAi): (IScope, Node) = {
    val factory = ai.chooseChild(possibleChildren, scope)
    val child = factory.create(scope)
    val updatedScope = factory.updateScope(scope)
    (updatedScope, child)
  }
}