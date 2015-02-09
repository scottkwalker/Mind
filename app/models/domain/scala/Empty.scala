package models.domain.scala

import com.google.inject.Inject
import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.UpdateScopeThrows

import scala.concurrent.Future

final case class Empty @Inject()() extends Instruction with UpdateScopeThrows {

  override def toRaw: String = throw new scala.RuntimeException

  override def hasNoEmpty(scope: IScope): Boolean = false

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Future[Instruction] =
    throw new scala.RuntimeException("cannot call replaceEmpty on an Empty type as it has no child nodes")

  override def height: Int = 0
}