package nodes

import nodes.helpers.Scope
import com.google.inject.Injector

trait Node {
  def toRawScala: String
  def validate(scope: Scope): Boolean
  def replaceEmpty(scope: Scope, injector: Injector): Node
}