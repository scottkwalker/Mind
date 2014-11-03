package ai

import models.common.IScope
import replaceEmpty.ReplaceEmpty
import utils.Timeout.finiteTimeout
import scala.concurrent.{Await, Future}

trait SelectionStrategy {

  protected val rng: RandomNumberGenerator

  def chooseChild(possibleChildren: Seq[ReplaceEmpty]): ReplaceEmpty

  def chooseIndex(seqLength: Int): Int

  def chooseChild(possibleChildren: Future[Seq[ReplaceEmpty]], scope: IScope): ReplaceEmpty = {
    val children = Await.result(possibleChildren, finiteTimeout)
    require(children.nonEmpty, s"Sequence possibleChildren must not be empty otherwise we cannot pick a node from it, contained: $possibleChildren")
    chooseChild(children)
  }

  def canAddAnother(accLength: Int,
                    factoryLimit: Int): Boolean = accLength < 1 || (accLength < factoryLimit && rng.nextBoolean)
}