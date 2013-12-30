package ai

import nodes.helpers._


trait AiCommon extends IAi {
  def chooseChild(possibleChildren: Seq[ICreateChildNodes], scope: IScope): ICreateChildNodes = {
    require(!possibleChildren.isEmpty, "Sequence possibleChildren must not be empty otherwise we cannot pick an node from it")
    chooseChild(possibleChildren)
  }

  def canAddAnother(accLength: Int,
                    factoryLimit: Int,
                    rng: IRandomNumberGenerator): Boolean = {
    accLength < 1 ||
      (accLength < factoryLimit && rng.nextBoolean())
  }
}