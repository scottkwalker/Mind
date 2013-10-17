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
    val (updatedScope, p) = replaceEmptyInSeq(scope, injector, params, funcCreateParams)
    val (_, n) = replaceEmptyInSeq(updatedScope, injector, nodes, funcCreateNodes)

    FunctionM(p, n, name)
  }

  private def funcCreateParams(scope: Scope, injector: Injector, premade: Seq[Node]): (Scope, Seq[Node]) = {
    val factory = injector.getInstance(classOf[FunctionMFactory])
    println("p")
    factory.createParams(scope = scope, acc = premade.init)
  }

  private def funcCreateNodes(scope: Scope, injector: Injector, premade: Seq[Node]): (Scope, Seq[Node]) = {
    val factory = injector.getInstance(classOf[FunctionMFactory])
    println("n")
    factory.createNodes(scope = scope, acc = premade.init)
  }

  @tailrec
  private def replaceEmptyInSeq(scope: Scope, injector: Injector, n: Seq[Node], f: ((Scope, Injector, Seq[Node]) => (Scope, Seq[Node])), acc: Seq[Node] = Seq[Node]()): (Scope, Seq[Node]) = {
    n match {
      case x :: xs => {
        val (updatedScope, replaced) = x match {
          case _: Empty => {
            f(scope, injector, n)
          }
          case n: Node => {
            val r = n.replaceEmpty(scope, injector)
            val u = r.updateScope(scope)
            (u, Seq(r))
          }
        }
        replaceEmptyInSeq(updatedScope, injector, xs, f, acc ++ replaced)
      }
      case nil => (scope, acc)
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

  def createParams(scope: Scope, acc: Seq[Node] = Seq[Node]()) = creator.createSeq(
    possibleChildren = paramsNeighbours,
    scope = scope,
    saveAccLengthInScope = Some((s: Scope, accLength: Int) => s.setNumVals(accLength)),
    acc = acc,
    factoryLimit = scope.maxParamsInFunc
  )

  def createNodes(scope: Scope, acc: Seq[Node] = Seq[Node]()) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    acc = acc,
    factoryLimit = scope.maxExpressionsInFunc
  )
}