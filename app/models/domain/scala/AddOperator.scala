package models.domain.scala

import com.google.inject.Injector
import factory.{UpdateScopeNoChange, ValueRefFactoryImpl}
import models.common.IScope
import models.domain.Node

final case class AddOperator(left: Node, right: Node) extends Node with UpdateScopeNoChange {

  override def toRaw: String = s"${left.toRaw} + ${right.toRaw}"

  override def hasNoEmpty(scope: IScope): Boolean = {
    def validate(n: Node, scope: IScope) = {
      n match {
        case _: ValueRef => n.hasNoEmpty(scope.decrementHeight)
        case _: Empty => false
        case _ => false
      }
    }

    if (scope.hasHeightRemaining) validate(left, scope) && validate(right, scope)
    else false
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Node = {
    def replaceEmpty(scope: IScope, n: Node): Node = {
      n match {
        case _: Empty => injector.getInstance(classOf[ValueRefFactoryImpl]).create(scope)
        case n: Node => n.replaceEmpty(scope.decrementHeight)
      }
    }

    val l = replaceEmpty(scope, left)
    val r = replaceEmpty(scope, right)
    AddOperator(l, r)
  }

  override def getMaxDepth: Int = 1 + math.max(left.getMaxDepth, right.getMaxDepth)
}
