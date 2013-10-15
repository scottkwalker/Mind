package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.util.Random
import scala.annotation.tailrec

case class FunctionM(params: Seq[Node],
                     nodes: Seq[Node],
                     name: String) extends Node with UpdateScopeIncrementFuncs {
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
    val p = replaceEmptyInSeq(scope, injector, params)
    val n = replaceEmptyInSeq(scope, injector, nodes)
    FunctionM(p, n, name)
  }

  @tailrec
  private def replaceEmptyInSeq(scope: Scope, injector: Injector, n: Seq[Node], acc: Seq[Node] = Seq[Node]()): Seq[Node] = {
    n match {
      case x :: xs => {
        val replaced = replaceEmpty(scope, injector, x)
        val updatedScope = replaced.updateScope(scope)
        replaceEmptyInSeq(updatedScope, injector, xs, acc ++ Seq(replaced))
      }
      case nil => acc
    }
  }

  private def replaceEmpty(scope: Scope, injector: Injector, n: Node): Node = {
    n match {
      case _: Empty => {
        val factory = injector.getInstance(classOf[FunctionMFactory])
        val (updatedScope, child) = factory.creator.createNode.create(factory.legalNeighbours(scope), scope, factory.ai) // TODO make this more generic in the factory.
        child
      }
      case n: Node => n.replaceEmpty(scope.incrementDepth, injector)
    }
  }

  override def getMaxDepth: Int = 1 + math.max(getMaxDepth(params), getMaxDepth(nodes))

  private def getMaxDepth(n: Seq[Node]): Int = n.map(_.getMaxDepth).reduceLeft(math.max)
}

case class FunctionMFactory @Inject()(injector: Injector,
                                      creator: CreateSeqNodes,
                                      ai: Ai,
                                      rng: Random) extends CreateChildNodes with UpdateScopeIncrementFuncs {
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