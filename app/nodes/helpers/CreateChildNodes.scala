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

  val neighbours: Seq[CreateChildNodes]

  def legalNeighbours(scope: Scope): Seq[CreateChildNodes] = neighbours.filter(n => n.canTerminateInStepsRemaining(scope.decrementStepsRemaining))

  def canTerminateInStepsRemaining(scope: Scope): Boolean = {
    if (scope.noStepsRemaining) false else {
      neighbours.exists(n => n.canTerminateInStepsRemaining(scope.decrementStepsRemaining))
    }
  }
}