package nodes

import com.google.inject.{Injector, Inject}
import nodes.helpers.Scope

case class Empty @Inject() () extends Node {
  override def toRawScala: String = throw new scala.RuntimeException
  override def validate(scope: Scope): Boolean = false
  override def replaceEmpty(scope: Scope, injector: Injector): Node = throw new scala.RuntimeException
}