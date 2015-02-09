package decision

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Step

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class CreateNodeImpl @Inject()(ai: SelectionStrategy) extends CreateNode {

  def create(possibleChildren: Future[Set[Decision]], scope: IScope): Future[(IScope, Step)] = async {
    val factory = await(ai.chooseChild(possibleChildren))
    val child = factory.createStep(scope)
    val updatedScope = factory.updateScope(scope)
    (updatedScope, await(child))
  }
}