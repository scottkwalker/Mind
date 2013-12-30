package nodes

import com.google.inject.{Injector, Inject}
import nodes.helpers.{IScope, Scope}

case class Empty @Inject()() extends Node with UpdateScopeThrows {
  override def toRawScala: String = throw new scala.RuntimeException

  override def validate(scope: IScope): Boolean = false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = throw new scala.RuntimeException

  override def getMaxDepth = 0
}