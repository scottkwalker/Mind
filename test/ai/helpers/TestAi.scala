package ai.helpers

import ai.Ai
import nodes.helpers._
import scala.util.Random

case class TestAi() extends Ai {
  override def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes = possibleChildren(0)
  override def canAddAnother(accLength: Int,
    factoryLimit: Int,
    rng: Random): Boolean = accLength < factoryLimit
}