package memoization

import replaceEmpty.ReplaceEmpty
import serialization.Versioning
import utils.PozInt

trait FactoryLookup extends Versioning {

  def convert(id: PozInt): ReplaceEmpty

  def convert(factory: ReplaceEmpty): PozInt
}
