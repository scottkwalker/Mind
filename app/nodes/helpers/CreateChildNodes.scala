package nodes.helpers

import nodes.Node
import nodes.NodeTree
import ai.Ai
import scala.annotation.tailrec

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

  def create(scope: Scope, ai: Ai): (Scope, Node) = {
    val factory = ai.chooseChild(this, scope)
    val updatedScope = factory.updateScope(scope)
    val child = factory.create(updatedScope)
    (updatedScope, child)
  }

  @tailrec
  final def createSeq(scope: Scope, ai: Ai, constraints: (Scope => Boolean), acc: Seq[Node]): Seq[Node] = {
    constraints(scope) match {
      case false => acc
      case true => {
        val (updatedScope, child) = create(scope, ai)

        createSeq(updatedScope.incrementAccumulatorLength,
          ai,
          constraints,
          acc ++ Seq(child))
      }
    }
  }
}