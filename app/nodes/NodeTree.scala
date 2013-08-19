package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject

class NodeTree(val rootNode: Node) extends Node {
  def toRawScala: String = rootNode.toRawScala

  def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) false else rootNode match {
    case _: ObjectM => rootNode.validate(scope.decrementStepsRemaining)
    case _: Empty => false
    case _ => false
  }
}

case class NodeTreeFactory @Inject() (injector: Injector) extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[ObjectMFactory]))

  def create(scope: Scope): Node = {
    val possibleChildren = validChildren(scope)
    val rootNode = possibleChildren(0).create(scope)
    new NodeTree(rootNode)
  }
}