package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.UpdateScopeNoChange

import scala.concurrent.Future

final case class IntegerM() extends Instruction with UpdateScopeNoChange {

  override def toRaw: String = "Int"

  override def hasNoEmpty(scope: IScope): Boolean = true

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Future[Instruction] = Future.successful(this)

  override def height = 1
}
