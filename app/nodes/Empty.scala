package nodes

import com.google.inject.Inject
import nodes.helpers.Scope

case class Empty @Inject() () extends Node {
  def toRawScala: String = throw new scala.RuntimeException
  def validate(scope: Scope): Boolean = false
}