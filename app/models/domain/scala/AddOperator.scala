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

  override def toCompilable: String = s"${left.toCompilable} + ${right.toCompilable}"

  override def hasNoEmptySteps(scope: IScope): Boolean = {
    def validate(instruction: Step, scope: IScope) = {
      instruction match {
        case _: ValueRef => instruction.hasNoEmptySteps(scope.decrementHeight)
        case _: Empty => false
        case _ => false
      }
    }

    scope.hasHeightRemaining && validate(left, scope) && validate(right, scope)
  }

  private def fillEmptySteps(scope: IScope, instruction: Step, factoryLookup: FactoryLookup): Future[Step] = {
    def decision = factoryLookup.convert(ValueRefFactory.id)
    instruction match {
      case _: Empty => decision.createStep(scope)
      case nonEmpty: Step => nonEmpty.fillEmptySteps(scope = scope.decrementHeight, factoryLookup = factoryLookup) // This node is non-empty but its children may not be, so do the same check on this node's children.
    }
  }

  override def fillEmptySteps(scope: IScope, factoryLookup: FactoryLookup): Future[Step] = async {
    val l = await(fillEmptySteps(scope = scope, instruction = left, factoryLookup = factoryLookup))
    val r = await(fillEmptySteps(scope = scope, instruction = right, factoryLookup = factoryLookup))

    AddOperator(l, r)
  }

  override def height: Int = 1 + math.max(left.height, right.height)
}
