package nodes.legalNeighbours

import nodes.helpers.ICreateChildNodes

trait FactoryIdToFactory {

  def convert(id: Int): ICreateChildNodes

  def convert(factory: ICreateChildNodes): Int
}
