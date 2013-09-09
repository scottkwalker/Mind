package nodes

import nodes.helpers.Scope

case class IntegerM() extends Node {
  override def toRawScala: String = "Int"
  override def validate(scope: Scope): Boolean = true
}
