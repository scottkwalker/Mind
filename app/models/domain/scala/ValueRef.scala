package models.domain.scala

import nodes.{UpdateScopeNoChange, Node}
import nodes.helpers.IScope
import com.google.inject.Injector

case class ValueRef(name: String) extends Node with UpdateScopeNoChange {
  override def toRaw: String = name

  override def validate(scope: IScope): Boolean = if (scope.hasDepthRemaining) !name.isEmpty else false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth = 1
}
