package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.util.Random

case class FunctionM(params: Seq[Node],
                     nodes: Seq[Node],
                     name: String) extends Node {
  override def toRawScala: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toRawScala).mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  }

  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) {
    !name.isEmpty &&
      nodes.forall(_ match {
        case n: AddOperator => n.validate(scope.incrementDepth)
        case n: ValueRef => n.validate(scope.incrementDepth)
        case _: Empty => false
        case _ => false
      })
  }
  else false
}

case class FunctionMFactory @Inject()(injector: Injector,
                                      creator: CreateSeqNodes,
                                      ai: Ai,
                                      rng: Random) extends CreateChildNodes {
  val paramsNeighbours: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[ValueInFunctionParamFactory])
  )

  val neighbours: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[AddOperatorFactory]),
    injector.getInstance(classOf[ValueRefFactory])
  )

  override def create(scope: Scope, premade: Option[Node] = None): Node = {
    val (updatedScope, params) = createParams(scope, premade)

    val (_, nodes) = createNodes(updatedScope, premade)

    FunctionM(params = params,
      nodes = nodes,
      name = "f" + scope.numFuncs)
  }

  private def createParams(scope: Scope, premade: Option[Node]) = creator.createSeq(
    possibleChildren = paramsNeighbours,
    scope = scope,
    ai = ai,
    constraints = (s: Scope, accLength: Int) => accLength < rng.nextInt(s.maxParamsInFunc),
    saveAccLengthInScope = Some((s: Scope, accLength: Int) => s.setNumVals(accLength)),
    premade = premade
  )

  private def createNodes(scope: Scope, premade: Option[Node]) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    ai = ai,
    constraints = (s: Scope, accLength: Int) => accLength < 1 + rng.nextInt(s.maxExpressionsInFunc),
    premade = premade
  )

  override def updateScope(scope: Scope) = scope.incrementFuncs
}