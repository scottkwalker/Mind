package decision

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Step
import models.domain.scala.FunctionMImpl
import models.domain.scala.FunctionMImpl$

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class FunctionMFactoryImpl @Inject() (
    creator: CreateSeqNodes,
    lookupChildren: LookupChildrenWithFutures) extends FunctionMFactory with UpdateScopeIncrementFuncs {

  override val nodesToChooseFrom = Set(AddOperatorFactory.id, ValueRefFactory.id)
  private val childrenToChooseFromForParams = Set(ValDclInFunctionParamFactory.id)

  override def fillEmptySteps(scope: IScope): Future[Step] = async {
    val paramsWithoutEmpties = await(createParams(scope))

    val nodesWithoutEmpties = await(createNodes(paramsWithoutEmpties.scope))

    FunctionMImpl(params = paramsWithoutEmpties.instructions,
      nodes = nodesWithoutEmpties.instructions,
      index = scope.numFuncs)
  }

  override def createParams(scope: IScope): Future[AccumulateInstructions] = {
    creator.create(
      possibleChildren = lookupChildren.get(scope, childrenToChooseFromForParams),
      scope = scope,
      acc = Seq.empty,
      factoryLimit = scope.maxParamsInFunc
    )
  }

  override def createNodes(scope: IScope): Future[AccumulateInstructions] = {
    creator.create(
      possibleChildren = lookupChildren.get(scope, nodesToChooseFrom),
      scope = scope,
      acc = Seq.empty,
      factoryLimit = scope.maxExpressionsInFunc
    )
  }
}