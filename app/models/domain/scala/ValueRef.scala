package models.domain.scala

import nodes.helpers.{UpdateScopeNoChange, IScope}
import com.google.inject.Injector
import models.domain.common.Node

case class ValueRef(name: String) extends Node with UpdateScopeNoChange {
  override def toRaw: String = name

  override def validate(scope: IScope): Boolean = if (scope.hasDepthRemaining) !name.isEmpty else false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth: Int = 1
}
