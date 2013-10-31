package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.util.Random
import scala.annotation.tailrec

case class NodeTree(nodes: Seq[Node]) extends Node with UpdateScopeThrows {
  override def toRawScala: String = nodes.map(f => f.toRawScala).mkString(" ")

  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) {
    nodes.forall {
      case n: ObjectDef => n.validate(scope.incrementDepth)
      case _: Empty => false
      case _ => false
    }
  }
  else false

  override def replaceEmpty(scope: Scope, injector: Injector): Node = {
    val (_, n) = replaceEmptyInSeq(scope, injector, nodes, funcCreateNodes)
    NodeTree(n)
  }

  private def funcCreateNodes(scope: Scope, injector: Injector, premade: Seq[Node]): (Scope, Seq[Node]) = {
    val factory = injector.getInstance(classOf[NodeTreeFactory])
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

  override def getMaxDepth = 1 + nodes.map(_.getMaxDepth).reduceLeft(math.max)
}

case class NodeTreeFactory @Inject()(injector: Injector,
                                     creator: CreateSeqNodes,
                                     ai: Ai,
                                     rng: Random,
                                     memoizeCanTerminateInStepsRemaining: MemoizeDi) extends CreateChildNodes with UpdateScopeThrows {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[ObjectDefFactory]))

  override def create(scope: Scope, premade: Option[Seq[CreateChildNodes]]): Node = {
    val (_, generated) = createNodes(scope)
    val nodes: Seq[Node] = premade match {
      case Some(pm) => generated ++ pm.map(p => p.create(scope))
      case _ => generated
    }

    NodeTree(nodes)
  }

  override def create(scope: Scope): Node = {
    val (_, nodes) = createNodes(scope)
    NodeTree(nodes)
  }

  def createNodes(scope: Scope, acc: Seq[Node] = Seq()): (Scope, Seq[Node]) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    saveAccLengthInScope = Some((s: Scope, accLength: Int) => s.setNumFuncs(accLength)),
    acc = acc,
    factoryLimit = scope.maxObjectsInTree
  )
}