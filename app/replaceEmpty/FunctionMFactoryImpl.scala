package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.FunctionM

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.async.Async.{async,await}
import scala.concurrent.Future

case class FunctionMFactoryImpl @Inject()(
                                           creator: CreateSeqNodes,
                                           legalNeighbours: LookupNeighbours
                                           ) extends FunctionMFactory with UpdateScopeIncrementFuncs {

  override val neighbourIds = Seq(AddOperatorFactoryImpl.id, ValueRefFactoryImpl.id)
  private val paramsNeighbours = Seq(ValDclInFunctionParamFactoryImpl.id)

  override def create(scope: IScope): Future[Instruction] = async {
    val (updatedScope, params) = await(createParams(scope))

    val (_, nodes) = await(createNodes(updatedScope))

    FunctionM(params = params,
      nodes = nodes,
      index = scope.numFuncs)
  }

  override def createParams(scope: IScope, acc: Seq[Instruction] = Seq.empty): Future[(IScope, Seq[Instruction])] = {
    creator.create(
      possibleChildren = legalNeighbours.fetch(scope, paramsNeighbours),
      scope = scope,
      saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumVals(accLength)),
      acc = acc,
      factoryLimit = scope.maxParamsInFunc
    )
  }

  override def createNodes(scope: IScope, acc: Seq[Instruction] = Seq.empty): Future[(IScope, Seq[Instruction])] = {
    creator.create(
      possibleChildren = legalNeighbours.fetch(scope, neighbourIds),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxExpressionsInFunc
    )
  }
}

object FunctionMFactoryImpl {

  val id = 2
}