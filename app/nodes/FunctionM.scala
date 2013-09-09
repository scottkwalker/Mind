package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai

case class FunctionM(params: Seq[Node],
                     nodes: Seq[Node],
                     name: String) extends Node {
  override def toRawScala: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toRawScala).mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  }

  override def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) {
    false
  }
  else {
    !name.isEmpty &&
      nodes.forall(_ match {
        case n: AddOperator => n.validate(scope.decrementStepsRemaining)
        case n: ValueRef => n.validate(scope.decrementStepsRemaining)
        case _: Empty => false
        case _ => false
      })
  }
}

case class FunctionMFactory @Inject()(injector: Injector,
                                      creator: CreateSeqNodes,
                                      ai: Ai) extends CreateChildNodes {
  val paramsLegalNeighbours: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[ValueInFunctionParamFactory])
  )

  val neighbours: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[AddOperatorFactory]),
    injector.getInstance(classOf[ValueRefFactory])
  )

  override def create(scope: Scope): Node = {
    val (updatedScope, params) = creator.create(paramsLegalNeighbours,
      scope = scope.resetAccumulator,
      ai = ai,
      constraints = seqConstraintsParams)

    val (_, nodes) = creator.create(
      possibleChildren = legalNeighbours(scope),
      scope = updatedScope.
        copyAccumulatorToNumVals.
        resetAccumulator,
      ai = ai,
      constraints = seqConstraintsNodes
    )

    FunctionM(params = params,//Seq(ValueInFunctionParam("v0", IntegerM()), ValueInFunctionParam("v1", IntegerM())),
      nodes = nodes,
      name = "f" + scope.numFuncs)
  }

  override def updateScope(scope: Scope) = scope.incrementFuncs

  private def seqConstraintsParams(scope: Scope): Boolean = scope.paramsHasSpaceForChildren

  private def seqConstraintsNodes(scope: Scope): Boolean = scope.funcHasSpaceForChildren
}