package ai.aco

import ai.RandomNumberGenerator
import ai.SelectionStrategy
import com.google.inject.Inject
import decision.Decision

// Ant Colony Optimisation
// https://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms
final case class Aco @Inject() (rng: RandomNumberGenerator) extends SelectionStrategy {

  override def chooseChild(possibleChildren: Set[Decision]): Decision = {
    require(possibleChildren.size > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    val index = chooseIndex(possibleChildren.size)
    possibleChildren.toSeq(index)
  }

  override def chooseIndex(seqLength: Int): Int = {
    require(seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    rng.nextInt(seqLength)
  }
}