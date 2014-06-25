package models.domain.scala

import com.google.inject.Injector
import models.domain.common.Node
import nodes.helpers.{IScope, UpdateScopeNoChange}

final case class ValueRef(name: String) extends Node with UpdateScopeNoChange {
  override def toRaw: String = name

  override def validate(scope: IScope): Boolean = if (scope.hasDepthRemaining) !name.isEmpty else false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth: Int = 1
}
