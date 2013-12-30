package ai

import nodes.helpers._
import scala.util.Random

trait Ai {
  def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes

  def chooseChild(possibleChildren: Seq[CreateChildNodes], scope: IScope): CreateChildNodes = {
    require(!possibleChildren.isEmpty, "Sequence possibleChildren must not be empty otherwise we cannot pick an node from it")
    chooseChild(possibleChildren)
  }

  def canAddAnother(accLength: Int,
                    factoryLimit: Int,
                    rng: Random): Boolean = {
    accLength < 1 ||
      (accLength < factoryLimit && rng.nextBoolean())
  }

  def chooseIndex(seqLength: Int): Int

  /*def canAddAnother(accLength: Int,
                    factoryLimit: Int,
                    rng: Random,
                    premade: Seq[CreateChildNodes]): Boolean = {
    val wildcardFound = premade.tail match {
      case _: FunctionMFactory => true // TODO Replace case with WildcardFactory
      case _ => false
    }

    if (!wildcardFound) accLength < premade.length // Not wildcard so copy all premade
    else {
      if (wildcardFound && accLength < premade.length) true // Wildcard so copy all premade and then random
      else canAddAnother(accLength, factoryLimit, rng)
    }
  }*/
}