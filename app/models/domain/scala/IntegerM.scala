package models.domain.scala

import com.google.inject.Injector
import models.domain.common.Node
import nodes.helpers.{IScope, UpdateScopeNoChange}

final case class IntegerM() extends Node with UpdateScopeNoChange {

  override def toRaw: String = "Int"

  override def validate(scope: IScope): Boolean = true

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth = 1
}
