package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.FunctionM
import scala.concurrent.Await

case class FunctionMFactoryImpl @Inject()(
                                           creator: CreateSeqNodes,
                                           legalNeighbours: LookupNeighbours
                                           ) extends FunctionMFactory with UpdateScopeIncrementFuncs {

  override val neighbourIds = Seq(AddOperatorFactoryImpl.id, ValueRefFactoryImpl.id)
  private val paramsNeighbours = Seq(ValDclInFunctionParamFactoryImpl.id)

  override def create(scope: IScope): Instruction = {
    val (updatedScope, params) = createParams(scope)

    val (_, nodes) = createNodes(updatedScope)

    FunctionM(params = params,
      nodes = nodes,
      index = scope.numFuncs)
  }

  def createParams(scope: IScope, acc: Seq[Instruction] = Seq.empty) = {
    creator.create(
      possibleChildren = Await.result(legalNeighbours.fetch(scope, paramsNeighbours), finiteTimeout),
      scope = scope,
      saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumVals(accLength)),
      acc = acc,
      factoryLimit = scope.maxParamsInFunc
    )
  }

  def createNodes(scope: IScope, acc: Seq[Instruction] = Seq.empty) = {
    creator.create(
      possibleChildren = Await.result(legalNeighbours.fetch(scope, neighbourIds), finiteTimeout),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxExpressionsInFunc
    )
  }
}

object FunctionMFactoryImpl {

  val id = 2
}