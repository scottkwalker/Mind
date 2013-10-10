package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.util.Random
import scala.annotation.tailrec

case class NodeTree(val nodes: Seq[Node]) extends Node with UpdateScopeThrows {
  override def toRawScala: String = nodes.map(f => f.toRawScala).mkString(" ")

  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) {
    nodes.forall(n => n match {
      case _: ObjectM => n.validate(scope.incrementDepth)
      case _: Empty => false
      case _ => false
    })
  }
  else false

  override def replaceEmpty(scope: Scope, injector: Injector): Node = {
    val n = replaceEmptyInSeq(scope, injector, nodes)
    NodeTree(n)
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
      case _: Empty => injector.getInstance(classOf[NodeTreeFactory]).create(scope)
      case n: Node => n.replaceEmpty(scope.incrementDepth, injector)
    }
  }

  override def getMaxDepth = 1 + nodes.map(_.getMaxDepth).reduceLeft(math.max)
}

case class NodeTreeFactory @Inject()(injector: Injector,
                                     creator: CreateSeqNodes,
                                     ai: Ai,
                                     rng: Random) extends CreateChildNodes with UpdateScopeThrows {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[ObjectMFactory]))

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

  private def createNodes(scope: Scope): (Scope, Seq[Node]) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    saveAccLengthInScope = Some((s: Scope, accLength: Int) => s.setNumFuncs(accLength)),
    factoryLimit = scope.maxObjectsInTree
  )
}