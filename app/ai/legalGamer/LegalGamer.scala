package ai.legalGamer

import ai.RandomNumberGenerator
import ai.SelectionStrategy
import com.google.inject.Inject
import decision.Decision

// Always chooses the first legal move available
final case class LegalGamer @Inject()(rng: RandomNumberGenerator)
    extends SelectionStrategy {

  override def chooseChild(possibleChildren: Set[Decision]): Decision = {
    require(
        possibleChildren.size > 0,
        "Sequence must not be empty otherwise we cannot pick an index from it")
    possibleChildren.head
  }

  override def chooseIndex(seqLength: Int): Int = {
    require(
        seqLength > 0,
        "Sequence must not be empty otherwise we cannot pick an index from it")
    0
  }

  override def canAddAnother(
      accLength: Int,
      factoryLimit: Int
  ): Boolean = accLength < factoryLimit
}
