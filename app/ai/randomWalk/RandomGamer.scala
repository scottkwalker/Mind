package ai.randomWalk

import ai.{SelectionStrategy, IRandomNumberGenerator}
import nodes.helpers._
import com.google.inject.Inject

// Always chooses a random move from the legal moves.
case class RandomGamer @Inject()(rng: IRandomNumberGenerator) extends SelectionStrategy {
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