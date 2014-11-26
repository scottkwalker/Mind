package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{IntegerMFactoryImpl, UpdateScopeIncrementVals}

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class ValDclInFunctionParam(name: String, primitiveType: Instruction) extends Instruction with UpdateScopeIncrementVals {

  override def toRaw: String = s"$name: ${primitiveType.toRaw}"

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && !name.isEmpty && {
    primitiveType match {
      case p: IntegerM => p.hasNoEmpty(scope)
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Future[Instruction] = async {
    val instruction = primitiveType match {
      case _: Empty => injector.getInstance(classOf[IntegerMFactoryImpl]).create(scope)
      case nonEmpty: Instruction => nonEmpty.replaceEmpty(updateScope(scope.decrementHeight))
    }
    ValDclInFunctionParam(name, await(instruction))
  }

  override def height: Int = 1 + primitiveType.height
}
