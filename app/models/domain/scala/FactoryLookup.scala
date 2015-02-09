package models.domain.scala

import decision.Decision
import serialization.Versioning
import utils.PozInt

trait FactoryLookup extends Versioning {

  val factories: Set[PozInt]

  def convert(id: PozInt): Decision

  def convert(factory: Decision): PozInt
}
