package ai

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope

trait Ai {
  def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes

  def chooseChild(factory: CreateChildNodes, scope: Scope): CreateChildNodes = {
    val possibleChildren = factory.validChildren(scope)

    if (possibleChildren.isEmpty) throw new scala.RuntimeException("Should not happen as we should have moved to a node has said that it or a descendent can terminate")

    chooseChild(possibleChildren)
  }
}