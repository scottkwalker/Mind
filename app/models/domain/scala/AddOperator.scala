package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{UpdateScopeNoChange, ValueRefFactory}
import utils.Timeout.finiteTimeout

import scala.concurrent.Await

final case class AddOperator(left: Instruction, right: Instruction) extends Instruction with UpdateScopeNoChange {

  override def toRaw: String = s"${left.toRaw} + ${right.toRaw}"

  override def hasNoEmpty(scope: IScope): Boolean = {
    def validate(n: Instruction, scope: IScope) = {
      n match {
        case _: ValueRef => n.hasNoEmpty(scope.decrementHeight)
        case _: Empty => false
        case _ => false
      }
    }

    scope.hasHeightRemaining && validate(left, scope) && validate(right, scope)
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = {
    def replaceEmpty(scope: IScope, child: Instruction) = {
      child match {
        case _: Empty => Await.result(factory.create(scope), finiteTimeout)
        case nonEmpty: Instruction => nonEmpty.replaceEmpty(scope.decrementHeight) // This node is non-empty but its children may not be, so do the same check on this node's children.
      }
    }

    lazy val factory = injector.getInstance(classOf[ValueRefFactory])
    val l = replaceEmpty(scope, left)
    val r = replaceEmpty(scope, right)
    AddOperator(l, r)
  }

  override def height: Int = 1 + math.max(left.height, right.height)
}
