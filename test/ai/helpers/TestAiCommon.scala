package ai.helpers

import ai.{IRandomNumberGenerator, AiCommon}
import nodes.helpers._
import com.google.inject.Inject

case class TestAiCommon @Inject()(rng: IRandomNumberGenerator) extends AiCommon {
  override def chooseChild(possibleChildren: Seq[ICreateChildNodes]): ICreateChildNodes = {
    require(possibleChildren.length > 0, "Sequence must not be empty otherwise we cannot pick an node from it")
    possibleChildren(0)
  }

  override def canAddAnother(accLength: Int,
                             factoryLimit: Int,
                             rng: IRandomNumberGenerator): Boolean = accLength < factoryLimit

  override def chooseIndex(seqLength: Int): Int = {
    require(seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    0
  }
}