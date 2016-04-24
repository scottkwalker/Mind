package decision

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Step

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class CreateNodeImpl @Inject() (ai: SelectionStrategy) extends CreateNode {

  def create(possibleChildren: Future[Set[Decision]], scope: IScope): Future[(IScope, Step)] = {
    ai.chooseChild(possibleChildren).flatMap { factory =>
      factory.fillEmptySteps(scope).map { child =>
        val updatedScope = factory.updateScope(scope)
        (updatedScope, child)
      }
    }
  }
}