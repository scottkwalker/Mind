package models.domain.scala

import com.google.inject.Injector
import factory.UpdateScopeNoChange
import models.common.IScope
import models.domain.Node

final case class ValueRef(name: String) extends Node with UpdateScopeNoChange {

  override def toRaw: String = name

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && !name.isEmpty

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Node = this

  override def height: Int = 1
}