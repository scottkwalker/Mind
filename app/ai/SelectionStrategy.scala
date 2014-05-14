package ai

import nodes.helpers._


trait SelectionStrategy extends IAi {
  implicit val rng: IRandomNumberGenerator

  def chooseChild(possibleChildren: Seq[ICreateChildNodes], scope: IScope): ICreateChildNodes = {
    require(!possibleChildren.isEmpty, "Sequence possibleChildren must not be empty otherwise we cannot pick an node from it")
    chooseChild(possibleChildren)
  }

  def canAddAnother(accLength: Int,
                    factoryLimit: Int): Boolean = {
    accLength < 1 ||
      (accLength < factoryLimit && rng.nextBoolean())
  }
}