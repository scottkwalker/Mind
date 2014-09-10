package memoization

import factory.ReplaceEmpty

trait FactoryLookup {

  def convert(id: Int): ReplaceEmpty

  def convert(factory: ReplaceEmpty): Int
}
