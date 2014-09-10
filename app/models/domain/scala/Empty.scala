package models.domain.scala

import com.google.inject.{Inject, Injector}
import factory.UpdateScopeThrows
import models.common.IScope
import models.domain.Node

final case class Empty @Inject()() extends Node with UpdateScopeThrows {

  override def toRaw: String = throw new scala.RuntimeException

  override def hasNoEmpty(scope: IScope): Boolean = false

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Node = throw new scala.RuntimeException

  override def getMaxDepth: Int = 0
}