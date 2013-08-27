package nodes

import com.google.inject.Inject
import nodes.helpers.Scope

case class Empty @Inject() () extends Node {
  override def toRawScala: String = throw new scala.RuntimeException
  override def validate(scope: Scope): Boolean = false
}