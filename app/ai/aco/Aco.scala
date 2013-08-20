package ai.aco

import ai.Ai
import nodes.helpers.CreateChildNodes

case class Aco() extends Ai {
  override def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes = possibleChildren(0)
}