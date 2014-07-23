package ai

import nodes.helpers.{ICreateChildNodes, IScope}

trait IAi {

  def chooseChild(possibleChildren: Seq[ICreateChildNodes]): ICreateChildNodes

  def chooseChild(possibleChildren: Seq[ICreateChildNodes], scope: IScope): ICreateChildNodes

  def canAddAnother(accLength: Int,
                    factoryLimit: Int): Boolean

  def chooseIndex(seqLength: Int): Int
}
