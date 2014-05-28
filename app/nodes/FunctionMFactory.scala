package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.FunctionM
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours


case class FunctionMFactory @Inject()(injector: Injector,
                                      creator: ICreateSeqNodes,
                                      ai: IAi,
                                      rng: IRandomNumberGenerator,
                                      legalNeighbours: LegalNeighbours
                                       ) extends ICreateChildNodes with UpdateScopeIncrementFuncs {
  private val paramsNeighbours: Seq[ICreateChildNodes] = Seq(
    injector.getInstance(classOf[ValDclInFunctionParamFactory])
  )

  override val neighbours = Seq(
    injector.getInstance(classOf[AddOperatorFactory]),
    injector.getInstance(classOf[ValueRefFactory])
  )
  override val neighbours2 = Seq(AddOperatorFactory.id, ValueRefFactory.id)

  override def create(scope: IScope): Node = {
    val (updatedScope, params) = createParams(scope)

    val (_, nodes) = createNodes(updatedScope)

    FunctionM(params = params,
      nodes = nodes,
      name = "f" + scope.numFuncs)
  }

  def createParams(scope: IScope, acc: Seq[Node] = Seq.empty) = creator.createSeq(
    possibleChildren = paramsNeighbours,
    scope = scope,
    saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumVals(accLength)),
    acc = acc,
    factoryLimit = scope.maxParamsInFunc
  )

  def createNodes(scope: IScope, acc: Seq[Node] = Seq.empty) = creator.createSeq(
    possibleChildren = legalNeighbours.fetch(scope, neighbours),
    scope = scope,
    acc = acc,
    factoryLimit = scope.maxExpressionsInFunc
  )
}

object FunctionMFactory {
  val id = 2
}