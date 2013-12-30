package ai.aco

import ai.{IRandomNumberGenerator, Ai}
import nodes.helpers._
import scala.util.Random
import com.google.inject.Inject


case class Aco @Inject()(rng: IRandomNumberGenerator) extends Ai {
  override def chooseChild(possibleChildren: Seq[ICreateChildNodes]): ICreateChildNodes = {
    require(possibleChildren.length > 0, "Sequence must not be empty otherwise we cannot pick an node from it")
    val index = rng.nextInt(possibleChildren.length)
    possibleChildren(index)
  }

  override def chooseIndex(seqLength: Int): Int = {
    require(seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    rng.nextInt(seqLength)
  }
}