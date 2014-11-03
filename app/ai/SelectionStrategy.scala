package ai

import models.common.IScope
import replaceEmpty.ReplaceEmpty
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait SelectionStrategy {

  protected val rng: RandomNumberGenerator

  def chooseChild(possibleChildren: Seq[ReplaceEmpty]): ReplaceEmpty

  def chooseIndex(seqLength: Int): Int

  def chooseChild(possibleChildren: Future[Seq[ReplaceEmpty]], scope: IScope): Future[ReplaceEmpty] = {
    possibleChildren map { children =>
      require(children.nonEmpty, s"Sequence possibleChildren must not be empty otherwise we cannot pick a node from it, contained: $possibleChildren")
      chooseChild(children)
    }
  }

  def canAddAnother(accLength: Int,
                    factoryLimit: Int): Boolean = accLength < 1 || (accLength < factoryLimit && rng.nextBoolean)
}