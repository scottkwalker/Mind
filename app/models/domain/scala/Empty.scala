package models.domain.scala

import com.google.inject.{Inject, Injector}
import models.domain.common.Node
import nodes.helpers.{IScope, UpdateScopeThrows}

final case class Empty @Inject()() extends Node with UpdateScopeThrows {

  override def toRaw: String = throw new scala.RuntimeException

  override def validate(scope: IScope): Boolean = false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = throw new scala.RuntimeException

  override def getMaxDepth: Int = 0
}