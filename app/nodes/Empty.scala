package nodes

import com.google.inject.Inject
import nodes.helpers.Scope

case class Empty @Inject() () extends Node {
  final override def toRawScala: String = throw new scala.RuntimeException
  final override def validate(scope: Scope): Boolean = false
}