package ai

import nodes.helpers._

trait Ai {
  def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes

  def chooseChild(possibleChildren: Seq[CreateChildNodes], scope: Scope): CreateChildNodes = {
    if (possibleChildren.isEmpty) throw new scala.RuntimeException("Should not happen as we should have moved to a node has said that it or a descendent can terminate")

    chooseChild(possibleChildren)
  }

  def canAddAnother(accLength: Int, factoryLimit: Int): Boolean = {
    accLength < factoryLimit
  }
}