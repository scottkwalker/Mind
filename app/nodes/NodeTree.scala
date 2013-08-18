package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject

class NodeTree(val rootNode: Node) extends Node {
  def toRawScala: String = rootNode.toRawScala

  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else rootNode match {
    case _: ObjectM => rootNode.validate(stepsRemaining - 1)
    case _: Empty => false
    case _ => false
  }
}

case class NodeTreeFactory @Inject() (injector: Injector) extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[ObjectMFactory]))

  def create(scope: Scope): Node = {
    new NodeTree(allPossibleChildren(0).create(scope))}
}