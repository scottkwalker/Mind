package replaceEmpty

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Instruction

final case class CreateNodeImpl @Inject()(ai: SelectionStrategy) extends CreateNode {

  def create(possibleChildren: Seq[ReplaceEmpty], scope: IScope): (IScope, Instruction) = {
    val factory = ai.chooseChild(possibleChildren, scope)
    val child = factory.create(scope)
    val updatedScope = factory.updateScope(scope)
    (updatedScope, child)
  }
}