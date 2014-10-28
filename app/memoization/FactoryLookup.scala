package memoization

import replaceEmpty.ReplaceEmpty
import serialization.Versioning
import scala.concurrent.Future

trait FactoryLookup extends Versioning {

  def convert(id: Int): ReplaceEmpty

  def convert(factory: ReplaceEmpty): Int
}
