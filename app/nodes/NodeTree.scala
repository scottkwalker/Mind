package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai

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

  override def create(scope: Scope = injector.getInstance(classOf[Scope])): Node = {
    val ai = injector.getInstance(classOf[Ai])
    val childFactory = ai.chooseChild(this, scope)
    val child = childFactory.create(scope)
    new NodeTree(child)
  }
  
  override def updateScope(scope: Scope) = throw new scala.RuntimeException("Should not happen as you cannot have more than one NodeTree")
}