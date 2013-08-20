package ai.helpers

import ai.Ai
import nodes.helpers.CreateChildNodes

case class TestAi() extends Ai {
  override def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes = possibleChildren(0)
}