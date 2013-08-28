package ai.helpers

import ai.Ai
import nodes.helpers._

case class TestAi() extends Ai {
  override def chooseChild(possibleChildren: Seq[FeasibleNodes]): CreateChildNodes = possibleChildren(0)
}