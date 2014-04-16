package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.FunctionM


case class FunctionMFactory @Inject()(injector: Injector,
                                      creator: ICreateSeqNodes,
                                      ai: IAi,
                                      rng: IRandomNumberGenerator
                                       ) extends ICreateChildNodes with UpdateScopeIncrementFuncs {
  val paramsNeighbours: Seq[ICreateChildNodes] = Seq(
    injector.getInstance(classOf[ValDclInFunctionParamFactory])
  )

  val neighbours: Seq[ICreateChildNodes] = Seq(
    injector.getInstance(classOf[AddOperatorFactory]),
    injector.getInstance(classOf[ValueRefFactory])
  )

  override def create(scope: IScope): Node = {
    val (updatedScope, params) = createParams(scope)

    val (_, nodes) = createNodes(updatedScope)

    FunctionM(params = params,
      nodes = nodes,
      name = "f" + scope.numFuncs)
  }

  def createParams(scope: IScope, acc: Seq[Node] = Seq[Node]()) = creator.createSeq(
    possibleChildren = paramsNeighbours,
    scope = scope,
    saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumVals(accLength)),
    acc = acc,
    factoryLimit = scope.maxParamsInFunc
  )

  def createNodes(scope: IScope, acc: Seq[Node] = Seq[Node]()) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    acc = acc,
    factoryLimit = scope.maxExpressionsInFunc
  )
}