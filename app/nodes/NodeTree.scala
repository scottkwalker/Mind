package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.annotation.tailrec

class NodeTree(val rootNode: Node) extends Node {
  override def toRawScala: String = rootNode.toRawScala

  override def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) false else rootNode match {
    case _: ObjectM => rootNode.validate(scope.decrementStepsRemaining)
    case _: Empty => false
    case _ => false
  }
}

case class NodeTreeFactory @Inject() (injector: Injector) extends FeasibleNodes {
  val allPossibleChildren: Seq[FeasibleNodes] = Seq(injector.getInstance(classOf[ObjectMFactory]))

  override def create(scope: Scope): Node = {
    val ai = injector.getInstance(classOf[Ai])
    val child = create(scope, ai)
    new NodeTree(child)
  }
    
  private def create(scope: Scope, ai: Ai): Node = {
    val factory = ai.chooseChild(this, scope)
    val updatedScope = factory.updateScope(scope)
    factory.create(updatedScope)
  }

  override def updateScope(scope: Scope) = throw new scala.RuntimeException("Should not happen as you cannot have more than one NodeTree")
}