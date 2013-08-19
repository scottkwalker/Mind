package ai

import nodes.helpers.CreateChildNodes

trait AI {
  def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes
}