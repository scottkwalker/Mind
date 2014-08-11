package nodes

import com.google.inject.Inject
import models.domain.common.Node
import models.domain.scala.FunctionM
import nodes.helpers._
import nodes.legalNeighbours.LegalNeighbours

trait FunctionMFactory extends ICreateChildNodes

case class FunctionMFactoryImpl @Inject()(
                                           creator: ICreateSeqNodes,
                                           legalNeighbours: LegalNeighbours
                                           ) extends FunctionMFactory with UpdateScopeIncrementFuncs {

  override val neighbourIds = Seq(AddOperatorFactoryImpl.id, ValueRefFactoryImpl.id)
  private val paramsNeighbours = Seq(ValDclInFunctionParamFactoryImpl.id)

  override def create(scope: IScope): Node = {
    val (updatedScope, params) = createParams(scope)

    val (_, nodes) = createNodes(updatedScope)

    FunctionM(params = params,
      nodes = nodes,
      name = "f" + scope.numFuncs)
  }

  def createParams(scope: IScope, acc: Seq[Node] = Seq.empty) = {
    creator.createSeq(
      possibleChildren = legalNeighbours.fetch(scope, paramsNeighbours),
      scope = scope,
      saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumVals(accLength)),
      acc = acc,
      factoryLimit = scope.maxParamsInFunc
    )
  }

  def createNodes(scope: IScope, acc: Seq[Node] = Seq.empty) = {
    creator.createSeq(
      possibleChildren = legalNeighbours.fetch(scope, neighbourIds),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxExpressionsInFunc
    )
  }
}

object FunctionMFactoryImpl {

  final val id = 2
}