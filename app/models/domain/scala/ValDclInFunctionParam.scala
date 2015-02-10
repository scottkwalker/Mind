package models.domain.scala

import com.google.inject.Injector
import decision.IntegerMFactory
import decision.IntegerMFactoryImpl
import decision.UpdateScopeIncrementVals
import models.common.IScope
import models.domain.Step

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class ValDclInFunctionParam(name: String, primitiveType: Step) extends Step with UpdateScopeIncrementVals {

  override def toCompilable: String = s"$name: ${primitiveType.toCompilable}"

  override def hasNoEmptySteps(scope: IScope): Boolean = scope.hasHeightRemaining && !name.isEmpty && {
    primitiveType match {
      case p: IntegerM => p.hasNoEmptySteps(scope)
      case _ => false
    }
  }

  override def fillEmptySteps(scope: IScope)(implicit injector: Injector): Future[Step] = async {
    val instruction = primitiveType match {
      case _: Empty =>
        def decision = injector.getInstance(classOf[FactoryLookup]).convert(IntegerMFactory.id)
        decision.createStep(scope)
      case nonEmpty: Step => nonEmpty.fillEmptySteps(updateScope(scope.decrementHeight))
    }
    ValDclInFunctionParam(name, await(instruction))
  }

  override def height: Int = 1 + primitiveType.height
}
