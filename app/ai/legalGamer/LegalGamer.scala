package ai.legalGamer

import ai.{IRandomNumberGenerator, SelectionStrategy}
import com.google.inject.Inject
import nodes.helpers._

// Always chooses the first legal move available
final case class LegalGamer @Inject()(rng: IRandomNumberGenerator) extends SelectionStrategy {

  override def chooseChild(possibleChildren: Seq[ICreateChildNodes]): ICreateChildNodes = {
    require(possibleChildren.length > 0, "Sequence must not be empty otherwise we cannot pick an node from it")
    possibleChildren(0)
  }

  override def canAddAnother(accLength: Int,
                             factoryLimit: Int): Boolean = accLength < factoryLimit

  override def chooseIndex(seqLength: Int): Int = {
    require(seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    0
  }
}