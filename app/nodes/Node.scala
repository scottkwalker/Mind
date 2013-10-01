package nodes

import nodes.helpers.Scope

trait Node {
  def toRawScala: String
  def validate(scope: Scope): Boolean
  def replaceEmpty(scope: Scope): Node
}