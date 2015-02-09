package ai.legalGamer

import ai.RandomNumberGenerator
import ai.SelectionStrategy
import com.google.inject.Inject
import replaceEmpty.ReplaceEmpty

// Always chooses the first legal move available
final case class LegalGamer @Inject()(rng: RandomNumberGenerator) extends SelectionStrategy {

  override def chooseChild(possibleChildren: Set[ReplaceEmpty]): ReplaceEmpty = {
    val index = chooseIndex(possibleChildren.size)
    possibleChildren.toSeq(index)
  }

  override def canAddAnother(accLength: Int,
                             factoryLimit: Int): Boolean = accLength < factoryLimit

  override def chooseIndex(seqLength: Int): Int = {
    require(seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    0
  }
}