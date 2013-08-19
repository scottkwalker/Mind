package nodes.helpers

import nodes.Node
import nodes.NodeTree
import ai.AI

trait CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes]
  def validChildren(scope: Scope) = allPossibleChildren.filter(n => n.couldTerminate(scope.decrementStepsRemaining))
  def couldTerminate(scope: Scope): Boolean = {
    if (scope.noStepsRemaining) false else {
      allPossibleChildren.exists(n => n.couldTerminate(scope.decrementStepsRemaining))
    }
  }
  def chooseChild(scope: Scope, ai: AI): CreateChildNodes = {
    val possibleChildren = validChildren(scope)
    ai.chooseChild(possibleChildren)
  }
  def create(scope: Scope): Node
}