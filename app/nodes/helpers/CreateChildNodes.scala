package nodes.helpers

import nodes.Node
import nodes.NodeTree
import ai.Ai

trait CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes]
  def validChildren(scope: Scope): Seq[CreateChildNodes] = allPossibleChildren.filter(n => n.couldTerminate(scope.decrementStepsRemaining))
  def couldTerminate(scope: Scope): Boolean = {
    if (scope.noStepsRemaining) false else {
      allPossibleChildren.exists(n => n.couldTerminate(scope.decrementStepsRemaining))
    }
  }
  def chooseChild(scope: Scope, ai: Ai): CreateChildNodes = {
    val possibleChildren = validChildren(scope)
    
    if (possibleChildren.isEmpty) throw new scala.RuntimeException("Should not happen as we should have moved to a node has said that it or a descendent can terminate")
    
    ai.chooseChild(possibleChildren)
  }
  def create(scope: Scope): Node
}