package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import models.domain.scala.FunctionM
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours


case class FunctionMFactory @Inject()(injector: Injector,
                                      creator: ICreateSeqNodes
                                       ) extends ICreateChildNodes with UpdateScopeIncrementFuncs {
  private val paramsNeighbours = Seq(ValDclInFunctionParamFactory.id)

  override val neighbourIds = Seq(AddOperatorFactory.id, ValueRefFactory.id)

  override def create(scope: IScope): Node = {
    val (updatedScope, params) = createParams(scope)

    val (_, nodes) = createNodes(updatedScope)

    FunctionM(params = params,
      nodes = nodes,
      name = "f" + scope.numFuncs)
  }

  def createParams(scope: IScope, acc: Seq[Node] = Seq.empty) = {
    val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
    creator.createSeq(
      possibleChildren = legalNeighbours.fetch(scope, paramsNeighbours),
      scope = scope,
      saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumVals(accLength)),
      acc = acc,
      factoryLimit = scope.maxParamsInFunc
    )
  }

  def createNodes(scope: IScope, acc: Seq[Node] = Seq.empty) = {
    val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
    creator.createSeq(
      possibleChildren = legalNeighbours.fetch(scope, neighbourIds),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxExpressionsInFunc
    )
  }
}

object FunctionMFactory {
  val id = 2
}