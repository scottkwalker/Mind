package decision

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Step
import models.domain.scala.ValDclInFunctionParam

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ValDclInFunctionParamFactoryImpl @Inject() (
    creator: CreateNode,
    lookupChildren: LookupChildrenWithFutures) extends ValDclInFunctionParamFactory with UpdateScopeIncrementVals {

  override val nodesToChooseFrom = Set(IntegerMFactory.id)

  override def fillEmptySteps(scope: IScope): Future[Step] = async {
    val name = "v" + scope.numVals
    val ln = lookupChildren.get(scope, nodesToChooseFrom)
    val (_, primitiveType) = await(creator.create(ln, scope))
    ValDclInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }

  override def createParams(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no params")

  override def createNodes(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no child nodes")
}