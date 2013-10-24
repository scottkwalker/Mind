package ai.helpers

import ai.Ai
import nodes.helpers._
import scala.util.Random
import com.google.inject.Inject

case class TestAi @Inject()(rng: Random) extends Ai {
  override def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes = possibleChildren(0)

  override def canAddAnother(accLength: Int,
                             factoryLimit: Int,
                             rng: Random): Boolean = accLength < factoryLimit

  override def chooseIndex(seqLength: Int): Int = {
    require(seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    rng.nextInt(seqLength)
  }
}