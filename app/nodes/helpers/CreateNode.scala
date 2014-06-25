package nodes.helpers

import ai.IAi
import com.google.inject.Inject
import models.domain.common.Node

final case class CreateNode @Inject()(ai: IAi) extends ICreateNode {
  def create(possibleChildren: Seq[ICreateChildNodes], scope: IScope): (IScope, Node) = {
    val factory = ai.chooseChild(possibleChildren, scope)
    val child = factory.create(scope)
    val updatedScope = factory.updateScope(scope)
    (updatedScope, child)
  }
}