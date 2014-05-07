package models.domain.scala

import nodes.helpers.{UpdateScopeNoChange, IScope}
import com.google.inject.Injector
import models.domain.common.Node

case class IntegerM() extends Node with UpdateScopeNoChange {
  override def toRaw: String = "Int"

  override def validate(scope: IScope): Boolean = true

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth = 1
}
