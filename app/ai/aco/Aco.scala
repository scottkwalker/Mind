package ai.aco

import ai.AI
import nodes.helpers.CreateChildNodes

case class ACO() extends AI {
  override def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes = possibleChildren(0)
}