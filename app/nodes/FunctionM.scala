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

  override def replaceEmpty(scope: Scope, injector: Injector): Node = {
    val p = params.map(p => replaceEmpty(scope, injector, p))
    val n = nodes.map(n => replaceEmpty(scope, injector, n))
    FunctionM(p, n, name)
  }

  private def replaceEmpty(scope: Scope, injector: Injector, n: Node): Node = {
    n match {
      case _: Empty => injector.getInstance(classOf[FunctionMFactory]).create(scope)
      case n: Node => n.replaceEmpty(scope, injector)
    }
  }
}

case class FunctionMFactory @Inject()(injector: Injector,
                                      creator: CreateSeqNodes,
                                      ai: Ai,
                                      rng: Random) extends CreateChildNodes with FunctionMUpdateScope {
  val paramsNeighbours: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[ValueInFunctionParamFactory])
  )

  val neighbours: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[AddOperatorFactory]),
    injector.getInstance(classOf[ValueRefFactory])
  )

  override def create(scope: Scope): Node = {
    val (updatedScope, params) = createParams(scope)

    val (_, nodes) = createNodes(updatedScope)

    FunctionM(params = params,
      nodes = nodes,
      name = "f" + scope.numFuncs)
  }

  private def createParams(scope: Scope) = creator.createSeq(
    possibleChildren = paramsNeighbours,
    scope = scope,
    saveAccLengthInScope = Some((s: Scope, accLength: Int) => s.setNumVals(accLength)),
    factoryLimit = scope.maxParamsInFunc
  )

  private def createNodes(scope: Scope) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    factoryLimit = scope.maxExpressionsInFunc
  )
}

trait FunctionMUpdateScope extends UpdateScope {
  override def updateScope(scope: Scope): Scope = scope.incrementFuncs
}