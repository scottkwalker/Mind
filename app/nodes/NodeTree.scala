package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai

class NodeTree(val rootNode: Node) extends Node {
  override def toRawScala: String = rootNode.toRawScala

  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) {
    rootNode match {
      case _: ObjectM => rootNode.validate(scope.incrementDepth)
      case _: Empty => false
      case _ => false
    }
  }
  else false
}

case class NodeTreeFactory @Inject()(injector: Injector,
                                     creator: CreateNode,
                                     ai: Ai) extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[ObjectMFactory]))

  override def create(scope: Scope): Node = {
    val (_, child) = creator.create(legalNeighbours(scope), scope, ai) // TODO make this a createSeq
    new NodeTree(child)
  }

  override def updateScope(scope: Scope) = throw new scala.RuntimeException("Should not happen as you cannot have more than one NodeTree")
}