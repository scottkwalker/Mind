package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Step
import decision.UpdateScopeNoChange

import scala.concurrent.Future

final case class IntegerM() extends Step with UpdateScopeNoChange {

  override def toRaw: String = "Int"

  override def hasNoEmpty(scope: IScope): Boolean = true

  override def fillEmptySteps(scope: IScope)(implicit injector: Injector): Future[Step] = Future.successful(this)

  override def height = 1
}
