package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Step
import decision.UpdateScopeNoChange

import scala.concurrent.Future

final case class ValueRef(name: String) extends Step with UpdateScopeNoChange {

  override def toCompilable: String = name

  override def hasNoEmptySteps(scope: IScope): Boolean = !name.isEmpty

  override def fillEmptySteps(scope: IScope)(implicit factoryLookup: FactoryLookup): Future[Step] = Future.successful(this)

  override def height: Int = 1
}

object ValueRef {

  def apply(index: Int): ValueRef = ValueRef(name = s"v$index")
}