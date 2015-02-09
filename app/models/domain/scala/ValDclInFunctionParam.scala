package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Step
import decision.IntegerMFactoryImpl
import decision.UpdateScopeIncrementVals

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class ValDclInFunctionParam(name: String, primitiveType: Step) extends Step with UpdateScopeIncrementVals {

  override def toRaw: String = s"$name: ${primitiveType.toRaw}"

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && !name.isEmpty && {
    primitiveType match {
      case p: IntegerM => p.hasNoEmpty(scope)
      case _ => false
    }
  }

  override def fillEmptySteps(scope: IScope)(implicit injector: Injector): Future[Step] = async {
    val instruction = primitiveType match {
      case _: Empty => injector.getInstance(classOf[IntegerMFactoryImpl]).createStep(scope)
      case nonEmpty: Step => nonEmpty.fillEmptySteps(updateScope(scope.decrementHeight))
    }
    ValDclInFunctionParam(name, await(instruction))
  }

  override def height: Int = 1 + primitiveType.height
}
