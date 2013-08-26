package nodes.helpers

import nodes.Node
import nodes.NodeTree
import ai.Ai
import scala.annotation.tailrec

trait CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes]
  
  def validChildren(scope: Scope): Seq[CreateChildNodes] = allPossibleChildren.filter(n => n.couldTerminate(scope.decrementStepsRemaining))
  
  def couldTerminate(scope: Scope): Boolean = {
    if (scope.noStepsRemaining) false else {
      allPossibleChildren.exists(n => n.couldTerminate(scope.decrementStepsRemaining))
    }
  }

  def create(scope: Scope): Node
  
  final def create(scope: Scope, ai: Ai): Node = {
    val factory = ai.chooseChild(this, scope)
    val updatedScope = factory.updateScope(scope)
    factory.create(updatedScope)
  }
  
  def updateScope(scope: Scope): Scope = scope
}