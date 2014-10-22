package ai

import models.common.IScope
import replaceEmpty.ReplaceEmpty

trait SelectionStrategy {

  protected val rng: RandomNumberGenerator

  def chooseChild(possibleChildren: Seq[ReplaceEmpty]): ReplaceEmpty

  def chooseIndex(seqLength: Int): Int

  def chooseChild(possibleChildren: Seq[ReplaceEmpty], scope: IScope): ReplaceEmpty = {
    require(possibleChildren.nonEmpty, s"Sequence possibleChildren must not be empty otherwise we cannot pick a node from it, contained: $possibleChildren")
    chooseChild(possibleChildren)
  }

  def canAddAnother(accLength: Int,
                    factoryLimit: Int): Boolean = accLength < 1 || (accLength < factoryLimit && rng.nextBoolean)
}