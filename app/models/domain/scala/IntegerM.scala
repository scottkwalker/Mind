package models.domain.scala

import decision.UpdateScopeNoChange
import models.common.IScope
import models.domain.Step

import scala.concurrent.Future

final case class IntegerM() extends PrimitiveType with UpdateScopeNoChange {

  override def toCompilable: String = "Int"

  override def hasNoEmptySteps(scope: IScope): Boolean = true

  override def fillEmptySteps(
      scope: IScope, factoryLookup: FactoryLookup): Future[Step] =
    Future.successful(this)

  override def height = 1
}
