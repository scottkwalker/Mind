package ai.aco

import ai.Ai
import nodes.helpers._
import scala.util.Random

case class Aco() extends Ai {
  override def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes = possibleChildren(0)
  override def chooseIndex(seqLength: Int, rng: Random): Int = 0
}