package ai.aco

import ai.Ai
import nodes.helpers._
import scala.util.Random
import com.google.inject.Inject


case class Aco @Inject()(rng: Random) extends Ai {
  override def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes = {
    possibleChildren(0)
  }

  override def chooseIndex(seqLength: Int): Int = 0
}