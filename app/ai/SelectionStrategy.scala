package ai

import models.common.IScope
import nodes.helpers._

trait SelectionStrategy {

  protected val rng: IRandomNumberGenerator

  def chooseChild(possibleChildren: Seq[ICreateChildNodes]): ICreateChildNodes

  def chooseIndex(seqLength: Int): Int

  def chooseChild(possibleChildren: Seq[ICreateChildNodes], scope: IScope): ICreateChildNodes = {
    require(possibleChildren.nonEmpty, "Sequence possibleChildren must not be empty otherwise we cannot pick a node from it")
    chooseChild(possibleChildren)
  }

  def canAddAnother(accLength: Int,
                    factoryLimit: Int): Boolean = accLength < 1 || (accLength < factoryLimit && rng.nextBoolean)
}