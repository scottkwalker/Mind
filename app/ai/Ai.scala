package ai

import nodes.helpers.CreateChildNodes

trait Ai {
  def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes
}