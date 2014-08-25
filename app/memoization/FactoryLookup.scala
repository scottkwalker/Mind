package memoization

import factory.ICreateChildNodes

trait FactoryLookup {

  def convert(id: Int): ICreateChildNodes

  def convert(factory: ICreateChildNodes): Int
}
