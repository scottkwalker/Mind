package nodes

import nodes.helpers.Scope
import scala.annotation.tailrec

trait Node {
  def toRawScala: String
  def validate(scope: Scope): Boolean
}