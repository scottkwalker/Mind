package ai.aco

import ai.{RandomNumberGenerator, SelectionStrategy}
import com.google.inject.Inject
import factory.ReplaceEmpty

// Ant Colony Optimisation
// https://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms
final case class Aco @Inject()(rng: RandomNumberGenerator) extends SelectionStrategy {

  override def chooseChild(possibleChildren: Seq[ReplaceEmpty]): ReplaceEmpty = {
    val index = chooseIndex(possibleChildren.length)
    possibleChildren(index)
  }

  override def chooseIndex(seqLength: Int): Int = {
    require(seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    rng.nextInt(seqLength)
  }
}