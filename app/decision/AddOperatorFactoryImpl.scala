package decision

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Step
import models.domain.scala.AddOperatorImpl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AddOperatorFactoryImpl @Inject() (
    creator: CreateNode,
    lookupChildren: LookupChildrenWithFutures) extends AddOperatorFactory with UpdateScopeNoChange {

  override val nodesToChooseFrom = Set(ValueRefFactory.id)

  override def fillEmptySteps(scope: IScope): Future[Step] = {
    val possibleNodes = lookupChildren.get(scope, nodesToChooseFrom)
    creator.create(possibleNodes, scope).flatMap {
      case (updatedScope, left) =>
        creator.create(possibleNodes, updatedScope).map {
          case (_, right) =>
            AddOperatorImpl(left, right)
        }
    }
  }

  override def createParams(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no params")

  override def createNodes(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no child nodes")
}