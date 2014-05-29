package models.domain.scala

import nodes._
import nodes.helpers.{UpdateScopeNoChange, IScope}
import com.google.inject.Injector
import models.domain.common.Node

final case class AddOperator(left: Node, right: Node) extends Node with UpdateScopeNoChange {
  override def toRaw: String = s"${left.toRaw} + ${right.toRaw}"

  override def validate(scope: IScope): Boolean = {
    def validate(n: Node, scope: IScope) = {
      n match {
        case _: ValueRef => n.validate(scope.incrementDepth)
        case _: Empty => false
        case _ => false
      }
    }

    if (scope.hasDepthRemaining) validate(left, scope) && validate(right, scope)
    else false
  }

  override def replaceEmpty(scope: IScope, injector: Injector): Node = {
    def replaceEmpty(scope: IScope, injector: Injector, n: Node): Node = {
      n match {
        case _: Empty => injector.getInstance(classOf[ValueRefFactoryImpl]).create(scope)
        case n: Node => n.replaceEmpty(scope.incrementDepth, injector)
      }
    }

    val l = replaceEmpty(scope, injector, left)
    val r = replaceEmpty(scope, injector, right)
    AddOperator(l, r)
  }

  override def getMaxDepth: Int = 1 + math.max(left.getMaxDepth, right.getMaxDepth)
}
