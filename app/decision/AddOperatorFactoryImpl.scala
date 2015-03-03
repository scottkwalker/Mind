package decision

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Step
import models.domain.scala.AddOperatorImpl

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AddOperatorFactoryImpl @Inject()(
                                             creator: CreateNode,
                                             lookupChildren: LookupChildrenWithFutures
                                             ) extends AddOperatorFactory with UpdateScopeNoChange {

  override val nodesToChooseFrom = Set(ValueRefFactory.id)

  override def createStep(scope: IScope): Future[Step] = async {
    val possibleNodes = lookupChildren.get(scope, nodesToChooseFrom)
    val (updatedScope, left) = await(creator.create(possibleNodes, scope))
    val (_, right) = await(creator.create(possibleNodes, updatedScope))
    AddOperatorImpl(left = left,
      right = right)
  }

  override def createParams(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no params")

  override def createNodes(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no child nodes")
}