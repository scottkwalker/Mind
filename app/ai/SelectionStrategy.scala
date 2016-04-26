package ai

import decision.Decision

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait SelectionStrategy {

  protected val rng: RandomNumberGenerator

  def chooseChild(possibleChildren: Set[Decision]): Decision

  def chooseIndex(seqLength: Int): Int

  def chooseChild(possibleChildren: Future[Set[Decision]]): Future[Decision] = {
    possibleChildren map { children =>
      require(
          children.nonEmpty,
          s"Sequence possibleChildren must not be empty otherwise we cannot pick a node from it, contained: $possibleChildren")
      chooseChild(children)
    }
  }

  def canAddAnother(
      accLength: Int,
      factoryLimit: Int
  ): Boolean = accLength == 0 || (accLength < factoryLimit && rng.nextBoolean)

  def generateLengthOfSeq(factoryLimit: Int): Integer = {
    require(factoryLimit > 0, "cannot make a random seq when limit is zero")
    val result = rng.nextInt(factoryLimit)
    result match {
      case 0 => 1
      case _ => result
    }
  }
}
