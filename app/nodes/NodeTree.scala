package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.util.Random

case class NodeTree(val nodes: Seq[Node]) extends Node {
  override def toRawScala: String = nodes.map(f => f.toRawScala).mkString(" ")

  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) {
    nodes.forall(n => n match {
      case _: ObjectM => n.validate(scope.incrementDepth)
      case _: Empty => false
      case _ => false
    })
  }
  else false
}

case class NodeTreeFactory @Inject()(injector: Injector,
                                     creator: CreateSeqNodes,
                                     ai: Ai,
                                     rng: Random) extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[ObjectMFactory]))

  override def create(scope: Scope, premade: Option[Seq[Node]]): Node = {
    val (_, generated) = createNodes(scope)
    val nodes: Seq[Node] = premade match {
      case Some(pm) => generated ++ pm
      case _ => generated
    }

    NodeTree(nodes)
  }

  override def create(scope: Scope): Node = {
    val (_, nodes) = createNodes(scope)
    NodeTree(nodes)
  }

  override def updateScope(scope: Scope): Scope = throw new scala.RuntimeException("Should not happen as you cannot have more than one NodeTree")

  private def createNodes(scope: Scope): (Scope, Seq[Node]) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    ai = ai,
    constraints = (s: Scope, accLength: Int) => accLength < 1 + rng.nextInt(s.maxObjectsInTree),
    saveAccLengthInScope = Some((s: Scope, accLength: Int) => s.setNumFuncs(accLength))
  )
}