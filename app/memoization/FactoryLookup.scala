package memoization

import replaceEmpty.ReplaceEmpty

trait FactoryLookup {

  def convert(id: Int): ReplaceEmpty

  def convert(factory: ReplaceEmpty): Int
}
