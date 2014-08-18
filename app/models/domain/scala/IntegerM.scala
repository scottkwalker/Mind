package models.domain.scala

import com.google.inject.Injector
import models.common.{IScope, Node}
import nodes.helpers.UpdateScopeNoChange

final case class IntegerM() extends Node with UpdateScopeNoChange {

  override def toRaw: String = "Int"

  override def validate(scope: IScope): Boolean = true

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Node = this

  override def getMaxDepth = 1
}
