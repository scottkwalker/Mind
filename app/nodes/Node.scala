package nodes

import nodes.helpers.Scope

trait Node {
  def toRawScala: String
  def validate(scope: Scope): Boolean
  def replaceWildcards(scope: Scope): Node = Empty()
}