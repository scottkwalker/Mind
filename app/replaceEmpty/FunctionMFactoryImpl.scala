package replaceEmpty

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.FunctionM
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.async.Async.{async,await}
import scala.concurrent.Future

case class FunctionMFactoryImpl @Inject()(
                                           creator: CreateSeqNodes,
                                           lookupChildren: LookupChildrenWithFutures
                                           ) extends FunctionMFactory with UpdateScopeIncrementFuncs {

  override val nodesToChooseFrom = Set(AddOperatorFactoryImpl.id, ValueRefFactoryImpl.id)
  private val childrenToChooseFromForParams = Set(ValDclInFunctionParamFactoryImpl.id)

  override def create(scope: IScope): Future[Instruction] = async {
    val paramsWithoutEmpties = await(createParams(scope))

    val nodesWithoutEmpties = await(createNodes(paramsWithoutEmpties.scope))

    FunctionM(params = paramsWithoutEmpties.instructions,
      nodes = nodesWithoutEmpties.instructions,
      index = scope.numFuncs)
  }

  override def createParams(scope: IScope): Future[AccumulateInstructions] = {
    creator.create(
      possibleChildren = lookupChildren.fetch(scope, childrenToChooseFromForParams),
      scope = scope,
      acc = Seq.empty,
      factoryLimit = scope.maxParamsInFunc
    )
  }

  override def createNodes(scope: IScope): Future[AccumulateInstructions] = {
    creator.create(
      possibleChildren = lookupChildren.fetch(scope, nodesToChooseFrom),
      scope = scope,
      acc = Seq.empty,
      factoryLimit = scope.maxExpressionsInFunc
    )
  }
}

object FunctionMFactoryImpl {

  val id = PozInt(2)
}