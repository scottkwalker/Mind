package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Step
import decision.UpdateScopeNoChange

import scala.concurrent.Future

final case class IntegerM() extends Step with UpdateScopeNoChange {

  override def toCompilable: String = "Int"

  override def hasNoEmptySteps(scope: IScope): Boolean = true

  override def fillEmptySteps(scope: IScope, factoryLookup: FactoryLookup): Future[Step] = Future.successful(this)

  override def height = 1
}
