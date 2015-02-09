package ai.randomWalk

import ai.RandomNumberGenerator
import ai.SelectionStrategy
import com.google.inject.Inject
import replaceEmpty.ReplaceEmpty

// Always chooses a random move from the legal moves.
final case class RandomGamer @Inject()(rng: RandomNumberGenerator) extends SelectionStrategy {

  override def chooseChild(possibleChildren: Set[ReplaceEmpty]): ReplaceEmpty = {
    val index = chooseIndex(possibleChildren.size)
    possibleChildren.toSeq(index)
  }

  override def chooseIndex(seqLength: Int): Int = {
    require(seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    rng.nextInt(seqLength)
  }
}