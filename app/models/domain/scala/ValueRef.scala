package models.domain.scala

import com.google.inject.Injector
import replaceEmpty.UpdateScopeNoChange
import models.common.IScope
import models.domain.Instruction

final case class ValueRef(name: String) extends Instruction with UpdateScopeNoChange {

  override def toRaw: String = name

//  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && !name.isEmpty
  override def hasNoEmpty(scope: IScope): Boolean = !name.isEmpty

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = this

  override def height: Int = 1
}

object ValueRef {
  def apply(index: Int): ValueRef = ValueRef(name = s"v$index")
}