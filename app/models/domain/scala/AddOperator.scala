package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Step
import decision.UpdateScopeNoChange
import decision.ValueRefFactory

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class AddOperator(left: Step, right: Step) extends Step with UpdateScopeNoChange {

  override def toRaw: String = s"${left.toRaw} + ${right.toRaw}"

  override def hasNoEmpty(scope: IScope): Boolean = {
    def validate(instruction: Step, scope: IScope) = {
      instruction match {
        case _: ValueRef => instruction.hasNoEmpty(scope.decrementHeight)
        case _: Empty => false
        case _ => false
      }
    }

    scope.hasHeightRemaining && validate(left, scope) && validate(right, scope)
  }

  private def fillEmptySteps(scope: IScope, instruction: Step)(implicit injector: Injector): Future[Step] = {
    lazy val factory = injector.getInstance(classOf[ValueRefFactory])
    instruction match {
      case _: Empty => factory.createStep(scope)
      case nonEmpty: Step => nonEmpty.fillEmptySteps(scope.decrementHeight) // This node is non-empty but its children may not be, so do the same check on this node's children.
    }
  }

  override def fillEmptySteps(scope: IScope)(implicit injector: Injector): Future[Step] = async {
    val l = await(fillEmptySteps(scope, left))
    val r = await(fillEmptySteps(scope, right))

    AddOperator(l, r)
  }

  override def height: Int = 1 + math.max(left.height, right.height)
}
