package models.domain.scala

import decision.ValueRefFactory
import models.common.IScope
import models.domain.Step

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class AddOperatorImpl(left: Step, right: Step) extends AddOperator {

  override def toCompilable: String =
    s"${left.toCompilable} + ${right.toCompilable}"

  override def hasNoEmptySteps(scope: IScope): Boolean = {
    def validate(instruction: Step, scope: IScope) = {
      instruction match {
        case _: ValueRef => instruction.hasNoEmptySteps(scope.decrementHeight)
        case _: Empty => false
        case _ => throw new RuntimeException("unhandled node type")
      }
    }

    scope.hasHeightRemaining && validate(left, scope) && validate(right, scope)
  }

  override def fillEmptySteps(
      scope: IScope, factoryLookup: FactoryLookup): Future[Step] =
    for {
      l <- fillEmptySteps(
          scope = scope, instruction = left, factoryLookup = factoryLookup)
      r <- fillEmptySteps(
          scope = scope, instruction = right, factoryLookup = factoryLookup)
    } yield AddOperatorImpl(l, r)

  private def fillEmptySteps(scope: IScope,
                             instruction: Step,
                             factoryLookup: FactoryLookup): Future[Step] = {
    def decision = factoryLookup.convert(ValueRefFactory.id)
    instruction match {
      case _: Empty => decision.fillEmptySteps(scope)
      case nonEmpty: Step =>
        nonEmpty.fillEmptySteps(
            scope = scope.decrementHeight,
            factoryLookup = factoryLookup) // This node is non-empty but its children may not be, so do the same check on this node's children.
    }
  }

  override def height: Int = 1 + math.max(left.height, right.height)
}
