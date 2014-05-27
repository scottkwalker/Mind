package models.domain.scala

import com.google.inject.{Injector, Inject}
import nodes.helpers.{UpdateScopeThrows, IScope}
import models.domain.common.Node

final case class Empty @Inject()() extends Node with UpdateScopeThrows {
  override def toRaw: String = throw new scala.RuntimeException

  override def validate(scope: IScope): Boolean = false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = throw new scala.RuntimeException

  override def getMaxDepth: Int = 0
}