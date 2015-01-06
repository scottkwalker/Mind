package models.domain.scala

import replaceEmpty.ReplaceEmpty
import serialization.Versioning
import utils.PozInt

trait FactoryLookup extends Versioning {

  val factories: Set[PozInt]

  def convert(id: PozInt): ReplaceEmpty

  def convert(factory: ReplaceEmpty): PozInt
}
