package models.domain.scala

import com.google.inject.{Inject, Injector}
import replaceEmpty.UpdateScopeThrows
import models.common.IScope
import models.domain.Instruction

final case class Empty @Inject()() extends Instruction with UpdateScopeThrows {

  override def toRaw: String = throw new scala.RuntimeException

  override def hasNoEmpty(scope: IScope): Boolean = false

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = throw new scala.RuntimeException

  override def height: Int = 0
}