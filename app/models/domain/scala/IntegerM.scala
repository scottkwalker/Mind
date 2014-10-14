package models.domain.scala

import com.google.inject.Injector
import replaceEmpty.UpdateScopeNoChange
import models.common.IScope
import models.domain.Instruction

final case class IntegerM() extends Instruction with UpdateScopeNoChange {

  override def toRaw: String = "Int"

  override def hasNoEmpty(scope: IScope): Boolean = true

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = this

  override def height = 1
}
