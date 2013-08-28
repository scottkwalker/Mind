package nodes.helpers

import nodes.Node
import nodes.NodeTree
import ai.Ai
import scala.annotation.tailrec

trait CreateChildNodes {
  def create(scope: Scope): Node
  
  def updateScope(scope: Scope): Scope = scope
}

trait FeasibleNodes extends CreateChildNodes {
  val allPossibleChildren: Seq[FeasibleNodes]
  
  def validChildren(scope: Scope): Seq[FeasibleNodes] = allPossibleChildren.filter(n => n.couldTerminate(scope.decrementStepsRemaining))
  
  def couldTerminate(scope: Scope): Boolean = {
    if (scope.noStepsRemaining) false else {
      allPossibleChildren.exists(n => n.couldTerminate(scope.decrementStepsRemaining))
    }
  }
}