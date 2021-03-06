package models.domain.scala

import decision.IntegerMFactory
import decision.UpdateScopeIncrementVals
import models.common.IScope
import models.domain.Step

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class ValDclInFunctionParam(name: String, primitiveType: Step)
    extends Step with UpdateScopeIncrementVals {

  override def toCompilable: String = s"$name: ${primitiveType.toCompilable}"

  override def hasNoEmptySteps(scope: IScope): Boolean =
    scope.hasHeightRemaining && !name.isEmpty && {
      primitiveType match {
        case p: IntegerM => p.hasNoEmptySteps(scope)
        case _ => false
      }
    }

  override def fillEmptySteps(
      scope: IScope, factoryLookup: FactoryLookup): Future[Step] = {
    val instruction = primitiveType match {
      case _: Empty =>
        def decision = factoryLookup.convert(IntegerMFactory.id)
        decision.fillEmptySteps(scope)
      case nonEmpty: PrimitiveType =>
        nonEmpty.fillEmptySteps(scope = updateScope(scope.decrementHeight),
                                factoryLookup = factoryLookup)
      case _ =>
        Future.failed(new RuntimeException(
                "the child needs to be either type Empty or type PrimitiveType"))
    }
    instruction.map(step => ValDclInFunctionParam(name, step))
  }

  override def height: Int = 1 + primitiveType.height
}
