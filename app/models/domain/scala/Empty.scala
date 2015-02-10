package models.domain.scala

import com.google.inject.Inject
import com.google.inject.Injector
import models.common.IScope
import models.domain.Step
import decision.UpdateScopeThrows

import scala.concurrent.Future

final case class Empty @Inject()() extends Step with UpdateScopeThrows {

  override def toCompilable: String = throw new scala.RuntimeException

  override def hasNoEmptySteps(scope: IScope): Boolean = false

  override def fillEmptySteps(scope: IScope, factoryLookup: FactoryLookup): Future[Step] =
    throw new scala.RuntimeException("cannot call fillEmptySteps on an Empty type as it has no child nodes")

  override def height: Int = 0
}