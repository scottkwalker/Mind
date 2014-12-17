package replaceEmpty

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Instruction
import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class CreateNodeImpl @Inject()(ai: SelectionStrategy) extends CreateNode {

  def create(possibleChildren: Future[Set[ReplaceEmpty]], scope: IScope): Future[(IScope, Instruction)] = async {
    val factory = await(ai.chooseChild(possibleChildren))
    val child = factory.create(scope)
    val updatedScope = factory.updateScope(scope)
    (updatedScope, await(child))
  }
}