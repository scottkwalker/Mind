package ai

import nodes.helpers._
import scala.util.Random
import nodes.FunctionMFactory

trait Ai {
  def chooseChild(possibleChildren: Seq[CreateChildNodes]): CreateChildNodes

  def chooseChild(possibleChildren: Seq[CreateChildNodes], scope: Scope): CreateChildNodes = {
    if (possibleChildren.isEmpty) throw new scala.RuntimeException("Should not happen as we should have moved to a node has said that it or a descendent can terminate")

    chooseChild(possibleChildren)
  }

  def canAddAnother(accLength: Int,
                    factoryLimit: Int,
                    rng: Random): Boolean = {
    accLength < 1 ||
      (accLength < factoryLimit && rng.nextBoolean())
  }

  def chooseIndex(seqLength: Int,
                  rng: Random): Int = {
    require (seqLength > 0, "Sequence must not be empty otherwise we cannot pick an index from it")
    rng.nextInt(seqLength)
  }

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