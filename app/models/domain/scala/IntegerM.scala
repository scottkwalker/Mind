package models.domain.scala

import com.google.inject.Injector
import factory.UpdateScopeNoChange
import models.common.IScope
import models.domain.Node

final case class IntegerM() extends Node with UpdateScopeNoChange {

  override def toRaw: String = "Int"

  override def hasNoEmpty(scope: IScope): Boolean = true

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Node = this

  override def height = 1
}
