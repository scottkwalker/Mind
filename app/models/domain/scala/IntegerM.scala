package models.domain.scala

import nodes.{UpdateScopeNoChange, Node}
import nodes.helpers.IScope
import com.google.inject.Injector

case class IntegerM() extends Node with UpdateScopeNoChange {
  override def toRawScala: String = "Int"

  override def validate(scope: IScope): Boolean = true

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth = 1
}
