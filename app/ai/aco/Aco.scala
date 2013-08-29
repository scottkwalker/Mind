package ai.aco

import ai.Ai
import nodes.helpers._

case class Aco() extends Ai {
  override def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes = possibleChildren(0)
}