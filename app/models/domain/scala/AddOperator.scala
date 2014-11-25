package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{UpdateScopeNoChange, ValueRefFactory}

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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

  private def replaceEmpty(scope: IScope, child: Instruction)(implicit injector: Injector): Future[Instruction] = {
    lazy val factory = injector.getInstance(classOf[ValueRefFactory])
    child match {
      case _: Empty => factory.create(scope)
      case nonEmpty: Instruction => nonEmpty.replaceEmpty(scope.decrementHeight) // This node is non-empty but its children may not be, so do the same check on this node's children.
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Future[Instruction] = async {
    val l = await(replaceEmpty(scope, left))
    val r = await(replaceEmpty(scope, right))

    AddOperator(l, r)
  }

  override def height: Int = 1 + math.max(left.height, right.height)
}
