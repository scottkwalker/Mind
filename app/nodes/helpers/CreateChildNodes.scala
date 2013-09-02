package nodes.helpers

import nodes.Node
import nodes.NodeTree
import ai.Ai
import scala.annotation.tailrec
import com.google.inject.Inject
import com.google.inject.Injector

trait CreateChildNodes {
  def create(scope: Scope): Node

  def updateScope(scope: Scope): Scope = scope

  val allPossibleChildren: Seq[CreateChildNodes]

  def validChildren(scope: Scope): Seq[CreateChildNodes] = allPossibleChildren.filter(n => n.couldTerminate(scope.decrementStepsRemaining))

  def couldTerminate(scope: Scope): Boolean = {
    if (scope.noStepsRemaining) false else {
      allPossibleChildren.exists(n => n.couldTerminate(scope.decrementStepsRemaining))
    }
  }
}