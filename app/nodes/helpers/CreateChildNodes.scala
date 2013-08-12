package nodes.helpers

import nodes.Node
import nodes.NodeTree

trait CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes]
  def validChildren(stepsRemaining: Integer) = allPossibleChildren.filter(n => n.couldTerminate(stepsRemaining - 1))
  def couldTerminate(stepsRemaining: Integer): Boolean = {
    if (stepsRemaining == 0) false else {
      allPossibleChildren.exists(n => n.couldTerminate(stepsRemaining - 1))
    }
  }
  def create(scope: Option[Scope] = None): Node
}