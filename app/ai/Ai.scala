package ai

import nodes.helpers._

trait Ai {
  def chooseChild(possibleChildren: Seq[FeasibleNodes]): CreateChildNodes

  def chooseChild(factory: FeasibleNodes, scope: Scope): CreateChildNodes = {
    val possibleChildren = factory.validChildren(scope)

    if (possibleChildren.isEmpty) throw new scala.RuntimeException("Should not happen as we should have moved to a node has said that it or a descendent can terminate")

    chooseChild(possibleChildren)
  }
}