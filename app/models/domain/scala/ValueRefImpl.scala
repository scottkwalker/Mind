package models.domain.scala

import models.common.IScope
import models.domain.Step

import scala.concurrent.Future

final case class ValueRefImpl(name: String) extends ValueRef {

  override def toCompilable: String = name

  override def hasNoEmptySteps(scope: IScope): Boolean = !name.isEmpty

  override def fillEmptySteps(scope: IScope, factoryLookup: FactoryLookup): Future[Step] = Future.successful(this)

  override def height: Int = 1
}

object ValueRefImpl {

  def apply(index: Int): ValueRefImpl = ValueRefImpl(name = s"v$index")
}