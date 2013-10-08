package nodes

import nodes.helpers._
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.util.Random
import scala.annotation.tailrec

case class ObjectM(nodes: Seq[Node], name: String) extends Node with UpdateScopeIncrementObjects {
  override def toRawScala: String = s"object $name ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"

  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) {
    nodes.forall(n => n match {
      case _: FunctionM => n.validate(scope.incrementDepth)
      case _: Empty => false
      case _ => false
    })
  }
  else false

  override def replaceEmpty(scope: Scope, injector: Injector): Node = {
    val n = replaceEmpty(scope, injector, nodes)//nodes.map(n => replaceEmpty(scope, injector, n))
    ObjectM(n, name)
  }

  private def replaceEmpty(scope: Scope, injector: Injector, n: Node): Node = {
    n match {
      case _: Empty => injector.getInstance(classOf[ObjectMFactory]).create(scope)
      case n: Node => n.replaceEmpty(scope.incrementDepth, injector)
    }
  }

  @tailrec
  private def replaceEmpty(scope: Scope, injector: Injector, n: Seq[Node], acc: Seq[Node] = Seq[Node]()): Seq[Node] = {
    n match {
      case x :: xs => replaceEmpty(updateScope(scope), injector, xs, acc ++ Seq(replaceEmpty(scope, injector, x)))
      case nil => acc
    }
  }

  override def getMaxDepth: Int = 1 + nodes.map(_.getMaxDepth).reduceLeft(math.max)
}

case class ObjectMFactory @Inject()(injector: Injector,
                                    creator: CreateSeqNodes,
                                    ai: Ai,
                                    rng: Random) extends CreateChildNodes with UpdateScopeIncrementObjects {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[FunctionMFactory]))

  override def create(scope: Scope): Node = {
    // TODO create object level val nodes?

    val (_, nodes) = createNodes(scope)

    ObjectM(nodes = nodes,
      name = "o" + scope.numObjects)
  }

  private def createNodes(scope: Scope) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    saveAccLengthInScope = Some((s: Scope, accLength: Int) => s.setNumFuncs(accLength)),
    factoryLimit = scope.maxFuncsInObject
  )
}