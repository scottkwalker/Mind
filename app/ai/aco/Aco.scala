package ai.aco

import ai.{RandomNumberGenerator, SelectionStrategy}
import com.google.inject.Inject
import factory.ICreateChildNodes

final case class Aco @Inject()(rng: RandomNumberGenerator) extends SelectionStrategy {

  override def chooseChild(possibleChildren: Seq[ICreateChildNodes]): ICreateChildNodes = {
    require(possibleChildren.length > 0, "Sequence must not be empty otherwise we cannot pick an node from it")
    val index = rng.nextInt(possibleChildren.length)
    possibleChildren(index)
  }

  override def chooseIndex(seqLength: Int): Int = {
    require(seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    rng.nextInt(seqLength)
  }
}